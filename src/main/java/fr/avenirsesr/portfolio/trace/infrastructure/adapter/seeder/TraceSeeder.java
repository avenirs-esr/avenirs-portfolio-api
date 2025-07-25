package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.AttachmentRepository;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.AttachmentMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.AttachmentEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake.FakeAttachment;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake.FakeTrace;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Getter
@Setter
public class TraceSeeder {
  private static final Faker faker = new FakerProvider().call();

  private final TraceRepository traceRepository;
  private final AttachmentRepository attachmentRepository;

  private UserEntity getRandomUserOf(List<UserEntity> users) {
    int randomIndex = faker.number().numberBetween(0, users.size());
    return users.get(randomIndex);
  }

  public List<TraceEntity> seed(List<UserEntity> users) {
    ValidationUtils.requireNonEmpty(users, "users cannot be empty");

    log.info("Seeding Traces...");

    List<TraceEntity> traceList = new ArrayList<>();
    List<AttachmentEntity> attachmentEntities = new ArrayList<>();

    for (int i = 0; i < SeederConfig.TRACES_NB; i++) {
      var fakeTrace = FakeTrace.of(getRandomUserOf(users));

      if (faker.random().nextBoolean()) fakeTrace = fakeTrace.withAiUseJustification();
      if (faker.random().nextBoolean()) fakeTrace = fakeTrace.withPersonalNote();
      if (faker.random().nextBoolean()) fakeTrace = fakeTrace.isGroup();

      var trace = fakeTrace.toEntity();
      traceList.add(trace);

      var nbOfVersions = faker.random().nextInt(1, SeederConfig.MAX_ATTACHMENT_PER_TRACE);
      for (int j = 1; j <= nbOfVersions; j++) {
        attachmentEntities.add(
            FakeAttachment.of(trace)
                .withVersion(j)
                .withIsActiveVersion(j == nbOfVersions)
                .toEntity());
      }
    }

    traceRepository.saveAll(traceList.stream().map(TraceMapper::toDomain).toList());
    attachmentRepository.saveAll(
        attachmentEntities.stream().map(AttachmentMapper::toDomain).toList());

    log.info("✔ {} traces created", traceList.size());

    return traceList;
  }
}
