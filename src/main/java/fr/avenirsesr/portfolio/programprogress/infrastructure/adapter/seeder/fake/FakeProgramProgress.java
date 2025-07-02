package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.programprogress.domain.model.Program;
import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.Student;
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
