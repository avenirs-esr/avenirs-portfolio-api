package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.port.output.seeder.SharedDataGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.domain.port.output.seeder.TraceDataGenerator;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class FakeTrace {
  private static final DataGeneratorProvider<SharedDataGenerator> sharedDataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeTrace.class, SharedDataGenerator.class);
  private static final DataGeneratorProvider<TraceDataGenerator> traceDataGenerator =
      new DataGeneratorProvider<TraceDataGenerator>()
          .init(FakeTrace.class, TraceDataGenerator.class);

  private final TraceEntity trace;

  private FakeTrace(TraceEntity trace) {
    this.trace = trace;
  }

  public static FakeTrace of(UserEntity user) {
    return new FakeTrace(
        TraceEntity.of(
            sharedDataGenerator.with("id").uuid(),
            user,
            traceDataGenerator.with("trace-title").traceName(),
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
    trace.setAiUseJustification(
        traceDataGenerator.with("setAiUseJustification").traceAiJustification());
    return this;
  }

  public FakeTrace withPersonalNote() {
    trace.setPersonalNote(traceDataGenerator.with("setPersonalNote").tracePersonalNote());
    return this;
  }

  public TraceEntity toEntity() {
    return trace;
  }
}
