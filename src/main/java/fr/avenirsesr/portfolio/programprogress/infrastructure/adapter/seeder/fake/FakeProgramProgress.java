package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.Set;
import java.util.UUID;

public class FakeProgramProgress {
  private static final FakerProvider faker = new FakerProvider();
  private final ProgramProgressEntity programProgress;

  private FakeProgramProgress(ProgramProgressEntity programProgress) {
    this.programProgress = programProgress;
  }

  public static FakeProgramProgress of(
      ProgramEntity program, UserEntity student, Set<SkillEntity> skills) {

    return new FakeProgramProgress(
        ProgramProgressEntity.of(
            UUID.fromString(faker.call().internet().uuid()), program, student, skills));
  }

  public ProgramProgressEntity toEntity() {
    return programProgress;
  }
}
