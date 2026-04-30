package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "skill_level",
    indexes = {
      @Index(name = "idx_skill_level_skill", columnList = "skill_id"),
      @Index(name = "idx_skill_level_training_path", columnList = "training_path_id"),
    })
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelEntity extends AvenirsBaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id")
  private SkillEntity skill;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "training_path_id")
  private TrainingPathEntity trainingPath;

  @OneToMany(
      mappedBy = "skillLevel",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<SkillLevelTranslationEntity> translations = new HashSet<>();

  private SkillLevelEntity(UUID id, SkillEntity skill, Instant createdAt, Instant updatedAt) {
    setId(id);
    this.skill = skill;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static SkillLevelEntity of(
      UUID id, SkillEntity skillEntity, Instant createdAt, Instant updatedAt) {
    return new SkillLevelEntity(id, skillEntity, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(getId());
  }
}
