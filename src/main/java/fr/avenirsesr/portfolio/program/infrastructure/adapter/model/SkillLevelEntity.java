package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level")
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

  private SkillLevelEntity(UUID id, SkillEntity skill) {
    setId(id);
    this.skill = skill;
  }

  public static SkillLevelEntity of(UUID id, SkillEntity skillEntity) {
    return new SkillLevelEntity(id, skillEntity);
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(getId());
  }
}
