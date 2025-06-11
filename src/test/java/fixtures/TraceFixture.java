package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeTrace;
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
  private boolean isGroup;
  private ELanguage language = ELanguage.FRENCH;

  private TraceFixture() {
    var fakeUser = UserFixture.create().toModel();
    var base = FakeTrace.of(fakeUser).toModel();
    this.id = base.getId();
    this.user = base.getUser();
    this.title = base.getTitle();
    this.skillLevels = base.getSkillLevels();
    this.amses = base.getAmses();
    this.createdAt = base.getCreatedAt();
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
    return Trace.toDomain(id, user, title, skillLevels, amses, isGroup, createdAt, language);
  }
}
