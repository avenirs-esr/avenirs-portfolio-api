package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "training_path")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainingPathEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  private ProgramEntity program;

  @ManyToMany
  @JoinTable(
      name = "training_path_skill_level",
      joinColumns = @JoinColumn(name = "training_path_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private Set<SkillLevelEntity> skillLevels;

  public TrainingPathEntity(UUID id, ProgramEntity program, Set<SkillLevelEntity> skillLevels) {
    this.setId(id);
    this.program = program;
    this.skillLevels = skillLevels;
  }

  public static TrainingPathEntity of(
      UUID id, ProgramEntity program, Set<SkillLevelEntity> skillLevels) {
    return new TrainingPathEntity(id, program, skillLevels);
  }

  public void addSkillLevel(SkillLevelEntity skillLevel) {
    this.skillLevels.add(skillLevel);
    if (skillLevel.getTrainingPaths() == null) {
      skillLevel.setTrainingPaths(new HashSet<>());
    }

    skillLevel.getTrainingPaths().add(this);
  }
}
