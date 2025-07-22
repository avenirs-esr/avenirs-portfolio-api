package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "student_additional_skill",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "additional_skill_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentAdditionalSkillEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private UserEntity student;

  @Column(name = "additional_skill_id")
  private String additionalSkillId;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private EAdditionalSkillType type;

  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus level;

  private StudentAdditionalSkillEntity(
      UUID id,
      UserEntity student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level) {
    setId(id);
    this.student = student;
    this.additionalSkillId = additionalSkillId;
    this.type = type;
    this.level = level;
  }

  public static StudentAdditionalSkillEntity of(
      UUID id,
      UserEntity student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level) {
    return new StudentAdditionalSkillEntity(id, student, additionalSkillId, type, level);
  }

  public static StudentAdditionalSkillEntity create(
      UserEntity student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level) {
    return new StudentAdditionalSkillEntity(
        UUID.randomUUID(), student, additionalSkillId, type, level);
  }
}
