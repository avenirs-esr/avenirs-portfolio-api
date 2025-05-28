package fr.avenirsesr.portfolio.api.domain.model;

import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgramProgress {
  private final UUID id;
  private final Program program;
  private final Student student;
  private final Set<Skill> skills;

  private ProgramProgress(UUID id, Program program, Student student, Set<Skill> skills) {
    this.id = id;
    this.program = program;
    this.student = student;
    this.skills = skills;
  }

  public static ProgramProgress create(
      UUID id, Program program, Student student, Set<Skill> skills) {
    return new ProgramProgress(id, program, student, skills);
  }

  public static ProgramProgress toDomain(
      UUID id, Program program, Student student, Set<Skill> skills) {
    return new ProgramProgress(id, program, student, skills);
  }
}
