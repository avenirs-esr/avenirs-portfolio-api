package fr.avenirsesr.portfolio.trace.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.FakeTrace;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TraceFixture {

  private UUID id;
  private User user;
  private String title;
  private List<SkillLevelProgress> skillLevels;
  private List<AdditionalSkillProgress> additionalSkillProgresses;
  private List<AMS> amses;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant deletedAt;
  private boolean isGroup;
  private String aiUseJustification;
  private String personalNote;
  private ELanguage language = ELanguage.FRENCH;

  private TraceFixture() {
    var fakeUser = UserFixture.create().toModel();
    var base = FakeTrace.of(UserMapper.INSTANCE.fromDomain(fakeUser)).toEntity();
    this.id = base.getId();
    this.user = fakeUser;
    this.title = base.getTitle();
    this.skillLevels =
        base.getSkillLevels().stream().map(SkillLevelProgressMapper.INSTANCE::toDomain).toList();
    this.amses = base.getAmses().stream().map(AMSMapper.INSTANCE::toDomain).toList();
    this.additionalSkillProgresses =
        base.getAdditionalSkillsProgresses().stream()
            .map(AdditionalSkillProgressMapper.INSTANCE::toDomain)
            .toList();
    this.createdAt = base.getCreatedAt();
    this.updatedAt = base.getUpdatedAt();
    this.deletedAt = base.getDeletedAt();
    this.isGroup = base.isGroup();
  }

  public static TraceFixture create() {
    return new TraceFixture();
  }

  public TraceFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public TraceFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public TraceFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public TraceFixture withSkillLevels(List<SkillLevelProgress> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public TraceFixture withAdditionalSkillProgresses(
      List<AdditionalSkillProgress> additionalSkillProgresses) {
    this.additionalSkillProgresses = additionalSkillProgresses;
    return this;
  }

  public TraceFixture withAmses(List<AMS> amses) {
    this.amses = amses;
    return this;
  }

  public TraceFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public TraceFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public TraceFixture withDeletedAt(Instant deletedAt) {
    this.deletedAt = deletedAt;
    return this;
  }

  public TraceFixture withGroup(boolean isGroup) {
    this.isGroup = isGroup;
    return this;
  }

  public TraceFixture withAiUseJustification(String aiUseJustification) {
    this.aiUseJustification = aiUseJustification;
    return this;
  }

  public TraceFixture withPersonalNote(String personalNote) {
    this.personalNote = personalNote;
    return this;
  }

  public List<Trace> withCount(int count) {
    List<Trace> traces = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      traces.add(create().toModel());
    }
    return traces;
  }

  public TraceFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public Trace toModel() {
    return Trace.toDomain(
        id,
        user,
        title,
        skillLevels,
        additionalSkillProgresses,
        amses,
        isGroup,
        aiUseJustification,
        personalNote,
        createdAt,
        updatedAt,
        deletedAt,
        language);
  }
}
