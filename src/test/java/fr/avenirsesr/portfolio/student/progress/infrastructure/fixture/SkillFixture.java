package fr.avenirsesr.portfolio.student.progress.infrastructure.fixture;

import fr.avenirsesr.portfolio.student.progress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake.FakeSkill;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.*;

public class SkillFixture {

  private UUID id;
  private String name;
  private Set<SkillLevel> skillLevels;
  private TrainingPath trainingPath;
  private ELanguage language = ELanguage.FRENCH;

  private SkillFixture() {
    Skill base =
        SkillMapper.toDomain(
            FakeSkill.of(Set.of(FakeSkillLevel.create().toEntity())).toEntity(), null);
    this.id = base.getId();
    this.name = base.getName();
    this.skillLevels = base.getSkillLevels();
    this.trainingPath = base.getTrainingPath();
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

  public SkillFixture withProgramProgress(TrainingPath trainingPath) {
    this.trainingPath = trainingPath;
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
    skill.setTrainingPath(trainingPath);
    return skill;
  }
}
