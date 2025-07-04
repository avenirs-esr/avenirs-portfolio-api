package fr.avenirsesr.portfolio.trace.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.fake.FakeTrace;
import fr.avenirsesr.portfolio.user.domain.model.User;
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
  private List<SkillLevel> skillLevels;
  private List<AMS> amses;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant deletedAt;
  private boolean isGroup;
  private ELanguage language = ELanguage.FRENCH;

  private TraceFixture() {
    var fakeUser = UserFixture.create().toModel();
    var base = FakeTrace.of(UserMapper.fromDomain(fakeUser)).toEntity();
    this.id = base.getId();
    this.user = fakeUser;
    this.title = base.getTitle();
    this.skillLevels =
        base.getSkillLevels().stream().map(SkillLevelMapper::toDomainWithoutRecursion).toList();
    this.amses = base.getAmses().stream().map(AMSMapper::toDomainWithoutRecursion).toList();
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

  public TraceFixture withSkillLevels(List<SkillLevel> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public TraceFixture withSkillLevels(int count) {
    this.skillLevels = SkillLevelFixture.create().withCount(count);
    return this;
  }

  public TraceFixture withAmses(List<AMS> amses) {
    this.amses = amses;
    return this;
  }

  public TraceFixture withAmses(int count) {
    this.amses = AMSFixture.create().withCount(count);
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
        id, user, title, skillLevels, amses, isGroup, createdAt, updatedAt, deletedAt, language);
  }
}
