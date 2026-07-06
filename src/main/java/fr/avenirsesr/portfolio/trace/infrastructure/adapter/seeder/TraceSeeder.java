package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.ESeederSource;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeTraceAttachment;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data.TraceAttachementCreationData;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data.TraceCreationData;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TraceSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(TraceSeeder.class, SharedDataGenerator.class);
  private static final String PATH_FILE = "seeder/traces.json";
  private final FileReader fileReader;
  private final TraceService traceService;
  private final FileResourceService fileResourceService;

  @Value("${seeder.source}")
  private ESeederSource seederSource;

  public TraceSeeder(
      FileReader fileReader,
      TraceService traceService,
      @Qualifier("MockFileResourceService") FileResourceService fileResourceService) {
    this.fileReader = fileReader;
    this.traceService = traceService;
    this.fileResourceService = fileResourceService;
  }

  @Transactional
  public List<TraceEntity> seed(List<UserEntity> users) {
    ValidationUtils.requireNonEmpty(users, "users cannot be empty");

    log.info("Seeding Traces...");

    List<TraceCreationData> creationData =
        switch (seederSource) {
          case CSV ->
              fileReader.readJSON(PATH_FILE, new TypeReference<List<TraceCreationData>>() {});
          case FAKER -> buildFakeTraces(users);
        };

    List<Trace> traces = new ArrayList<>();

    creationData.forEach(
        data -> {
          var trace =
              traceService.createTrace(
                  data.traceId(),
                  data.userId(),
                  data.title(),
                  data.language(),
                  data.authorType(),
                  data.personalNote(),
                  data.aiJustification(),
                  data.link(),
                  data.valorized());
          traces.add(trace);

          RequestContext.set(
              new RequestData(Optional.ofNullable(trace.getUser()), ELanguage.FRENCH));
          data.attachements()
              .forEach(
                  attachment -> {
                    fileResourceService.upload(
                        trace.getId(),
                        EFileCategory.TRACE_ATTACHMENT,
                        attachment.title(),
                        attachment.fileType().getMimeType(),
                        attachment.size(),
                        null);
                  });
        });

    log.info("✔ {} traces created", creationData.size());
    return traces.stream().map(TraceMapper.INSTANCE::fromDomain).toList();
  }

  private List<TraceCreationData> buildFakeTraces(List<UserEntity> users) {
    return IntStream.range(0, SeederConfig.TRACES_NB_MAX)
        .mapToObj(i -> users.get(new Random().nextInt(users.size())))
        .map(u -> FakeTrace.of(u).toEntity())
        .map(
            entity ->
                new TraceCreationData(
                    entity.getId(),
                    entity.getUser().getId(),
                    entity.getTitle(),
                    entity.getAuthorType(),
                    entity.getLanguage(),
                    entity.getAiUseJustification(),
                    entity.getPersonalNote(),
                    entity.getLink(),
                    entity.isValorized(),
                    entity.getLink() == null
                        ? IntStream.range(
                                1,
                                dataGenerator
                                    .with("number")
                                    .number(SeederConfig.MAX_ATTACHMENT_PER_TRACE))
                            .mapToObj(i -> FakeTraceAttachment.of(entity).toEntity())
                            .map(
                                attachmentEntity ->
                                    new TraceAttachementCreationData(
                                        attachmentEntity.getFileName(),
                                        attachmentEntity.getFileType(),
                                        attachmentEntity.getSize(),
                                        attachmentEntity.getUploadedAt()))
                            .toList()
                        : null))
        .toList();
  }
}
