package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
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
  private Set<SkillLevelEntity> skillsLevels;

  public TrainingPathEntity(
      UUID id, ProgramEntity program, Set<SkillLevelEntity> skillsLevels) {
    this.setId(id);
    this.program = program;
    this.skillsLevels = skillsLevels;
  }

  public static ProgramProgressEntity of(
      UUID id, ProgramEntity program, UserEntity student, Set<SkillEntity> skills) {
    return new ProgramProgressEntity(id, program, student, skills);
  }
}
