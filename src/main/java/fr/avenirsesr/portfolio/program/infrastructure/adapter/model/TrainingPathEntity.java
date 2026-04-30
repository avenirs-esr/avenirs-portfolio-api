package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "training_path",
    indexes = {@Index(name = "idx_training_path_program", columnList = "program_id")})
@NoArgsConstructor
@Getter
@Setter
public class TrainingPathEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  private ProgramEntity program;

  @OneToMany(mappedBy = "trainingPath", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SkillLevelEntity> skillLevels;

  private TrainingPathEntity(
      UUID id,
      ProgramEntity program,
      Set<SkillLevelEntity> skillLevels,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.program = program;
    this.skillLevels = skillLevels;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static TrainingPathEntity of(
      UUID id,
      ProgramEntity program,
      Set<SkillLevelEntity> skillLevels,
      Instant createdAt,
      Instant updatedAt) {
    return new TrainingPathEntity(id, program, skillLevels, createdAt, updatedAt);
  }
}
