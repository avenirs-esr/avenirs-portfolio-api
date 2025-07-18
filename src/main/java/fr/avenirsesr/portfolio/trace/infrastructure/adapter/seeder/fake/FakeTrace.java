package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.datafaker.Faker;

public class FakeTrace {
  private static final Faker faker = new FakerProvider().call();
  private final TraceEntity trace;

  private FakeTrace(TraceEntity trace) {
    this.trace = trace;
  }

  public static FakeTrace of(UserEntity user) {
    return new FakeTrace(
        TraceEntity.of(
            UUID.fromString(faker.internet().uuid()),
            user,
            faker.lorem().sentence(),
            ELanguage.FALLBACK,
            List.of(),
            List.of(),
            false,
            null,
            null,
            Instant.now(),
            Instant.now(),
            null));
  }

  public FakeTrace withSkillLevel(List<SkillLevelProgressEntity> skillLevels) {
    trace.setSkillLevels(skillLevels);
    skillLevels.forEach(
        skillLevel ->
            skillLevel.setTraces(
                Stream.concat(skillLevel.getTraces().stream(), Stream.of(trace)).toList()));
    return this;
  }

  public FakeTrace withAMS(List<AMSEntity> amses) {
    trace.setAmses(amses);
    return this;
  }

  public FakeTrace withELanguage(ELanguage language) {
    trace.setLanguage(language);
    return this;
  }

  public FakeTrace withDeletedAt(Instant deletedAt) {
    trace.setDeletedAt(deletedAt);
    return this;
  }

  public FakeTrace isGroup() {
    trace.setGroup(true);
    return this;
  }

  public FakeTrace withAiUseJustification() {
    trace.setAiUseJustification("I used AI because : %s".formatted(faker.lorem().sentence(5)));
    return this;
  }

  public FakeTrace withPersonalNote() {
    trace.setPersonalNote("My notes : %s".formatted(faker.lorem().sentence(5)));
    return this;
  }

  public TraceEntity toEntity() {
    return trace;
  }
}
