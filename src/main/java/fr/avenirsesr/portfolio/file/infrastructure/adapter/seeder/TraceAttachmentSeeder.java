package fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.TraceAttachmentMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.fake.FakeTraceAttachment;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceAttachmentSeeder {
  private static final FakerProvider faker = new FakerProvider().init(TraceAttachmentSeeder.class);

  private final TraceAttachmentRepository attachmentRepository;

  @Transactional
  public List<TraceAttachmentEntity> seed(List<TraceEntity> traces) {
    ValidationUtils.requireNonEmpty(traces, "traces cannot be empty");

    log.info("Seeding trace attachments...");

    List<TraceAttachmentEntity> attachmentEntities = new ArrayList<>();
    for (TraceEntity trace : traces) {
      var nbOfVersions =
          faker.call("number").random().nextInt(1, SeederConfig.MAX_ATTACHMENT_PER_TRACE);
      for (int j = 1; j <= nbOfVersions; j++) {
        attachmentEntities.add(
            FakeTraceAttachment.of(trace)
                .withVersion(j)
                .withIsActiveVersion(j == nbOfVersions)
                .toEntity());
      }
    }

    attachmentRepository.saveAll(
        attachmentEntities.stream().map(TraceAttachmentMapper::toDomain).toList());
    log.info("✔ {} traces attachments created", attachmentEntities.size());
    return attachmentEntities;
  }
}
