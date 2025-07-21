package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingPath extends AvenirsBaseModel {
  private final Program program;
  private Set<SkillLevel> skillLevels;

  private TrainingPath(UUID id, Program program, Set<SkillLevel> skillLevels) {
    super(id);
    this.program = program;
    this.skillLevels = skillLevels;
  }

  public static TrainingPath create(UUID id, Program program, Set<SkillLevel> skills) {
    return new TrainingPath(id, program, skills);
  }

  public static TrainingPath toDomain(UUID id, Program program, Set<SkillLevel> skills) {
    return new TrainingPath(id, program, skills);
  }
}
