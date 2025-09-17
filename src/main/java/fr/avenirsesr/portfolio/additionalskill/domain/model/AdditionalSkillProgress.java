package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
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
      UUID id,
      Student student,
      AdditionalSkill skill,
      EAdditionalSkillLevel level,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.skill = skill;
    this.level = level;
  }

  public static AdditionalSkillProgress create(
      Student student, AdditionalSkill skill, EAdditionalSkillLevel level) {
    return new AdditionalSkillProgress(
        UUID.randomUUID(), student, skill, level, Instant.now(), Instant.now());
  }

  public static AdditionalSkillProgress toDomain(
      UUID id,
      Student student,
      AdditionalSkill skill,
      EAdditionalSkillLevel level,
      Instant createdAt,
      Instant updatedAt) {
    return new AdditionalSkillProgress(id, student, skill, level, createdAt, updatedAt);
  }
}
