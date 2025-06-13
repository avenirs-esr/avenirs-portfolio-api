package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeSkillLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillLevelFixture {

  private UUID id;
  private String name;
  private String description;
  private ESkillLevelStatus status;
  private List<Trace> traces;
  private List<AMS> amses;
  private Skill skill;
  private ELanguage language = ELanguage.FRENCH;

  private SkillLevelFixture() {
    SkillLevel base = FakeSkillLevel.create().toModel();
    this.id = base.getId();
    this.name = base.getName();
    this.description = base.getDescription();
    this.status = base.getStatus();
    this.traces = base.getTraces();
    this.amses = base.getAmses();
    this.skill = base.getSkill();
  }

  public static SkillLevelFixture create() {
    return new SkillLevelFixture();
  }

  public SkillLevelFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillLevelFixture withName(String name) {
    this.name = name;
    return this;
  }

  public SkillLevelFixture withStatus(ESkillLevelStatus status) {
    this.status = status;
    return this;
  }

  public SkillLevelFixture withTrace(List<Trace> traces) {
    this.traces = traces;
    return this;
  }

  public SkillLevelFixture withAmses(List<AMS> amses) {
    this.amses = amses;
    return this;
  }

  public SkillLevelFixture withSkill(Skill skill) {
    this.skill = skill;
    return this;
  }

  public List<SkillLevel> withCount(int count) {
    List<SkillLevel> skillLevels = new ArrayList<SkillLevel>();
    for (int i = 0; i < count; i++) {
      skillLevels.add(create().toModel());
    }
    return skillLevels;
  }

  public SkillLevelFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public SkillLevelFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public SkillLevel toModel() {
    return SkillLevel.toDomain(id, name, description, status, traces, amses, skill, language);
  }
}
