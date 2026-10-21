package fr.avenirsesr.portfolio.student.skill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillProgress extends AvenirsBaseModel {
  private final Student student;
  private final Skill skill;
  private ESkillLevel level;
  private String reflection;
  private boolean valorized;

  private SkillProgress(
      UUID id,
      Student student,
      Skill skill,
      ESkillLevel level,
      String reflection,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.skill = skill;
    this.level = level;
    this.reflection = reflection;
    this.valorized = valorized;
  }

  public static SkillProgress create(
      Student student, Skill skill, ESkillLevel level, String reflection) {
    return new SkillProgress(
        UUID.randomUUID(), student, skill, level, reflection, false, Instant.now(), Instant.now());
  }

  public static SkillProgress toDomain(
      UUID id,
      Student student,
      Skill skill,
      ESkillLevel level,
      String reflection,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    return new SkillProgress(
        id, student, skill, level, reflection, valorized, createdAt, updatedAt);
  }
}
