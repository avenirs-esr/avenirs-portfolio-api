package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
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

  @OneToMany(mappedBy = "trainingPath", cascade = CascadeType.ALL, orphanRemoval = true)
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
}
