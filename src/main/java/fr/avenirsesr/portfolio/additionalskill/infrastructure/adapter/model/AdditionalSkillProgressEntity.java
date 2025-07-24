package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
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
    name = "additional_skill_progress",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "additional_skill_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdditionalSkillProgressEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private UserEntity student;

  @Column(name = "additional_skill_id")
  private UUID additionalSkillId;

  @Column(name = "level")
  @Enumerated(EnumType.STRING)
  private EAdditionalSkillLevel level;

  private AdditionalSkillProgressEntity(
      UUID id, UserEntity student, UUID additionalSkillId, EAdditionalSkillLevel level) {
    setId(id);
    this.student = student;
    this.additionalSkillId = additionalSkillId;
    this.level = level;
  }

  public static AdditionalSkillProgressEntity of(
      UUID id, UserEntity student, UUID additionalSkillId, EAdditionalSkillLevel level) {
    return new AdditionalSkillProgressEntity(id, student, additionalSkillId, level);
  }

  public static AdditionalSkillProgressEntity create(
      UserEntity student, UUID additionalSkillId, EAdditionalSkillLevel level) {
    return new AdditionalSkillProgressEntity(UUID.randomUUID(), student, additionalSkillId, level);
  }
}
