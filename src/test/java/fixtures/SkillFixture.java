package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.fake.FakeSkill;
import java.util.*;

public class SkillFixture {

  private UUID id;
  private String name;
  private Set<SkillLevel> skillLevels;
  private ProgramProgress programProgress;
  private ELanguage language = ELanguage.FRENCH;

  private SkillFixture() {
    Skill base = FakeSkill.of(Set.of(SkillLevelFixture.create().toModel())).toModel();
    this.id = base.getId();
    this.name = base.getName();
    this.skillLevels = base.getSkillLevels();
    this.programProgress = base.getProgramProgress();
  }

  public static SkillFixture create() {
    return new SkillFixture();
  }

  public SkillFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillFixture withName(String name) {
    this.name = name;
    return this;
  }

  public SkillFixture withSkillLevels(Set<SkillLevel> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public SkillFixture withSkillLevels(int count) {
    this.skillLevels = new HashSet<>(SkillLevelFixture.create().withCount(count));
    return this;
  }

  public SkillFixture withProgramProgress(ProgramProgress programProgress) {
    this.programProgress = programProgress;
    return this;
  }

  public List<Skill> withCount(int count) {
    List<Skill> skills = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      skills.add(create().toModel());
    }
    return skills;
  }

  public SkillFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public Skill toModel() {
    Skill skill = Skill.toDomain(id, name, skillLevels);
    skill.setProgramProgress(programProgress);
    return skill;
  }
}
