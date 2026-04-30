package fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "declared_skill_progress",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "declared_skill_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeclaredSkillProgressEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "declared_skill_id", nullable = true)
  private DeclaredSkillEntity declaredSkill;

  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private EDeclaredSkillLevel level;

  @Column(length = RICH_TEXT_LENGTH)
  private String reflection;

  private DeclaredSkillProgressEntity(
      UUID id,
      StudentEntity student,
      DeclaredSkillEntity declaredSkill,
      EDeclaredSkillLevel level,
      String reflection,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    this.student = student;
    this.declaredSkill = declaredSkill;
    this.level = level;
    this.reflection = reflection;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static DeclaredSkillProgressEntity of(
      UUID id,
      StudentEntity student,
      DeclaredSkillEntity declaredSkill,
      EDeclaredSkillLevel level,
      String reflection,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredSkillProgressEntity(
        id, student, declaredSkill, level, reflection, createdAt, updatedAt);
  }
}
