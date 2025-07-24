package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkillProgress extends AvenirsBaseModel {
  private final Student student;
  private final AdditionalSkill skill;
  private final EAdditionalSkillLevel level;

  private AdditionalSkillProgress(
      UUID id, Student student, AdditionalSkill skill, EAdditionalSkillLevel level) {
    super(id);
    this.student = student;
    this.skill = skill;
    this.level = level;
  }

  public static AdditionalSkillProgress create(
      Student student, AdditionalSkill skill, EAdditionalSkillLevel level) {
    return new AdditionalSkillProgress(UUID.randomUUID(), student, skill, level);
  }

  public static AdditionalSkillProgress toDomain(
      UUID id, Student student, AdditionalSkill skill, EAdditionalSkillLevel level) {
    return new AdditionalSkillProgress(id, student, skill, level);
  }
}
