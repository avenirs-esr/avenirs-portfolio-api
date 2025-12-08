package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "additional_skill_progress",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "additional_skill_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdditionalSkillProgressEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "additional_skill_id", nullable = true)
  private AdditionalSkillEntity additionalSkill;

  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private EAdditionalSkillLevel level;

  @Column(length = 400)
  private String description;

  private AdditionalSkillProgressEntity(
      UUID id,
      StudentEntity student,
      AdditionalSkillEntity additionalSkill,
      EAdditionalSkillLevel level,
      String description) {
    setId(id);
    this.student = student;
    this.additionalSkill = additionalSkill;
    this.level = level;
    this.description = description;
  }

  public static AdditionalSkillProgressEntity of(
      UUID id,
      StudentEntity student,
      AdditionalSkillEntity additionalSkill,
      EAdditionalSkillLevel level,
      String description) {
    return new AdditionalSkillProgressEntity(id, student, additionalSkill, level, description);
  }

  public static AdditionalSkillProgressEntity create(
      UUID id,
      StudentEntity student,
      AdditionalSkillEntity additionalSkill,
      EAdditionalSkillLevel level,
      String description) {
    return new AdditionalSkillProgressEntity(id, student, additionalSkill, level, description);
  }
}
