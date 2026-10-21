package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
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
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_declared_skill_progress_student_skill",
          columnNames = {"student_id", "skill_id"})
    })
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeclaredSkillProgressEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id")
  private SkillEntity skill;

  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private EDeclaredSkillLevel level;

  @Column(length = RICH_TEXT_LENGTH)
  private String reflection;

  @Column(nullable = false)
  private boolean valorized;

  private DeclaredSkillProgressEntity(
      UUID id,
      StudentEntity student,
      SkillEntity skill,
      EDeclaredSkillLevel level,
      String reflection,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    this.student = student;
    this.skill = skill;
    this.level = level;
    this.reflection = reflection;
    this.valorized = valorized;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static DeclaredSkillProgressEntity of(
      UUID id,
      StudentEntity student,
      SkillEntity skill,
      EDeclaredSkillLevel level,
      String reflection,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredSkillProgressEntity(
        id, student, skill, level, reflection, valorized, createdAt, updatedAt);
  }
}
