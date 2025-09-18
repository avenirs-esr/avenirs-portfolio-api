package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingPath extends AvenirsBaseModel {
  private final Program program;
  private Set<SkillLevel> skillLevels;

  private TrainingPath(
      UUID id, Program program, Set<SkillLevel> skillLevels, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.program = program;
    this.skillLevels = skillLevels;
  }

  public static TrainingPath create(UUID id, Program program, Set<SkillLevel> skills) {
    return new TrainingPath(id, program, skills, Instant.now(), Instant.now());
  }

  public static TrainingPath toDomain(
      UUID id, Program program, Set<SkillLevel> skills, Instant createdAt, Instant updatedAt) {
    return new TrainingPath(id, program, skills, createdAt, updatedAt);
  }
}
