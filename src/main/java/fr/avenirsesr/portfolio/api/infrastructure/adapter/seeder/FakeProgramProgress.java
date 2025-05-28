package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Program;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import java.util.Set;
import java.util.UUID;

public class FakeProgramProgress {
  private static final FakerProvider faker = new FakerProvider();
  private final ProgramProgress programProgress;

  private FakeProgramProgress(ProgramProgress programProgress) {
    this.programProgress = programProgress;
  }

  public static FakeProgramProgress of(Program program, Student student, Set<Skill> skills) {
    return new FakeProgramProgress(
        ProgramProgress.create(
            UUID.fromString(faker.call().internet().uuid()), program, student, skills));
  }

  public ProgramProgress toModel() {
    return programProgress;
  }
}
