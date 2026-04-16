package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.datafaker.Faker;

public class FakeTrace {
  private final TraceEntity trace;

  private static Faker faker() {
    return new Faker();
  }

  private FakeTrace(TraceEntity trace) {
    this.trace = trace;
  }

  public static FakeTrace of(UserEntity user) {
    var fakeTrace =
        new FakeTrace(
            TraceEntity.of(
                UUID.randomUUID(),
                user,
                "Trace %s".formatted(faker().lorem().word()),
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

    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withAiUseJustification();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.withPersonalNote();
    if (new Random().nextBoolean()) fakeTrace = fakeTrace.isGroup();

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
    trace.setAiUseJustification("I use AI for : %s".formatted(faker().lorem().sentence(5)));
    return this;
  }

  public FakeTrace withPersonalNote() {
    trace.setPersonalNote("Personal note : %s".formatted(faker().lorem().sentence(5)));
    return this;
  }

  public TraceEntity toEntity() {
    return trace;
  }
}
