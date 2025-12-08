package fr.avenirsesr.portfolio.student.progress.imported.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
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
  private EAdditionalSkillLevel level;
  private String description;

  private AdditionalSkillProgress(
      UUID id,
      Student student,
      AdditionalSkill skill,
      EAdditionalSkillLevel level,
      String description,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.skill = skill;
    this.level = level;
    this.description = description;
  }

  public static AdditionalSkillProgress create(
      Student student, AdditionalSkill skill, EAdditionalSkillLevel level, String description) {
    return new AdditionalSkillProgress(
        UUID.randomUUID(), student, skill, level, description, Instant.now(), Instant.now());
  }

  public static AdditionalSkillProgress toDomain(
      UUID id,
      Student student,
      AdditionalSkill skill,
      EAdditionalSkillLevel level,
      String description,
      Instant createdAt,
      Instant updatedAt) {
    return new AdditionalSkillProgress(
        id, student, skill, level, description, createdAt, updatedAt);
  }
}
