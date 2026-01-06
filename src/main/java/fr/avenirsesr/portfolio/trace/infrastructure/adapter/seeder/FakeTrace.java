package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.domain.port.output.seeder.TraceDataGenerator;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;

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
    var fakeTrace =
        new FakeTrace(
            TraceEntity.of(
                sharedDataGenerator.with("id").uuid(),
                user,
                traceDataGenerator.with("trace-title").traceName(),
                ELanguage.FALLBACK,
                List.of(),
                List.of(),
                List.of(),
                false,
                null,
                null,
                Instant.now(),
                Instant.now(),
                null));

    if (sharedDataGenerator.with("withAiUseJustification").bool())
      fakeTrace = fakeTrace.withAiUseJustification();
    if (sharedDataGenerator.with("withPersonalNote").bool())
      fakeTrace = fakeTrace.withPersonalNote();
    if (sharedDataGenerator.with("isGroup").bool()) fakeTrace = fakeTrace.isGroup();

    return fakeTrace;
  }

  public FakeTrace withSkillLevel(List<SkillLevelProgressEntity> skillLevels) {
    trace.setSkillLevels(skillLevels);
    return this;
  }

  public FakeTrace withDeclaredSkillsProgress(
      List<DeclaredSkillProgressEntity> declaredSkillsProgress) {
    trace.setDeclaredSkillsProgresses(declaredSkillsProgress);
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
