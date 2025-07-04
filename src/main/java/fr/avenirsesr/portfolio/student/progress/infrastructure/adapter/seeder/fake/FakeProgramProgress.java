package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.student.progress.domain.model.Program;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.student.progress.domain.model.Skill;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.Set;
import java.util.UUID;

public class FakeProgramProgress {
  private static final FakerProvider faker = new FakerProvider();
  private final TrainingPath trainingPath;

  private FakeProgramProgress(TrainingPath trainingPath) {
    this.trainingPath = trainingPath;
  }

  public static FakeProgramProgress of(Program program, Student student, Set<Skill> skills) {
    return new FakeProgramProgress(
        TrainingPath.create(
            UUID.fromString(faker.call().internet().uuid()), program, student, skills));
  }

  public TrainingPath toModel() {
    return trainingPath;
  }
}
