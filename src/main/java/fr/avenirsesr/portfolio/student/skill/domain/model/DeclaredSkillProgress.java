package fr.avenirsesr.portfolio.student.skill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclaredSkillProgress extends AvenirsBaseModel {
  private final Student student;
  private final DeclaredSkill skill;
  private EDeclaredSkillLevel level;
  private String reflection;
  private boolean valorized;

  private DeclaredSkillProgress(
      UUID id,
      Student student,
      DeclaredSkill skill,
      EDeclaredSkillLevel level,
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

  public static DeclaredSkillProgress create(
      Student student, DeclaredSkill skill, EDeclaredSkillLevel level, String reflection) {
    return new DeclaredSkillProgress(
        UUID.randomUUID(), student, skill, level, reflection, false, Instant.now(), Instant.now());
  }

  public static DeclaredSkillProgress toDomain(
      UUID id,
      Student student,
      DeclaredSkill skill,
      EDeclaredSkillLevel level,
      String reflection,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredSkillProgress(
        id, student, skill, level, reflection, valorized, createdAt, updatedAt);
  }
}
