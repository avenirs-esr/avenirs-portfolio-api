package fr.avenirsesr.portfolio.program.domain.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingPath {
  private final UUID id;
  private final Program program;
  private Set<SkillLevel> skillLevels;

  private TrainingPath(UUID id, Program program, Set<SkillLevel> skillLevels) {
    this.id = id;
    this.program = program;
    this.skillLevels = skillLevels;
  }

  public static TrainingPath create(UUID id, Program program, Set<SkillLevel> skills) {
    return new TrainingPath(id, program, skills);
  }

  public static TrainingPath toDomain(UUID id, Program program, Set<SkillLevel> skills) {
    return new TrainingPath(id, program, skills);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TrainingPath that)) return false;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
