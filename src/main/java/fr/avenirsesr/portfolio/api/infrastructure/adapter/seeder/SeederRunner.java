package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeederRunner implements CommandLineRunner {
  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  private final UserRepository userRepository;
  private final ExternalUserRepository externalUserRepository;
  private final InstitutionRepository institutionRepository;
  private final ProgramRepository programRepository;
  private final ProgramProgressRepository programProgressRepository;
  private final SkillLevelRepository skillLevelRepository;
  private final SkillRepository skillRepository;
  private final TrackRepository trackRepository;
  private final AMSRepository amsRepository;

  public SeederRunner(
      UserRepository userRepository,
      ExternalUserRepository externalUserRepository,
      InstitutionRepository institutionRepository,
      ProgramRepository programRepository,
      ProgramProgressRepository programProgressRepository,
      SkillLevelRepository skillLevelRepository,
      SkillRepository skillRepository,
      TrackRepository trackRepository,
      AMSRepository amsRepository) {
    this.userRepository = userRepository;
    this.externalUserRepository = externalUserRepository;
    this.institutionRepository = institutionRepository;
    this.programRepository = programRepository;
    this.programProgressRepository = programProgressRepository;
    this.skillLevelRepository = skillLevelRepository;
    this.skillRepository = skillRepository;
    this.trackRepository = trackRepository;
    this.amsRepository = amsRepository;
  }

  @Override
  public void run(String... args) {
    if (seedEnabled) {
      log.info("Seeder is enabled: seeding stared");

      var fakeUsers =
          List.of(
              FakeUser.create().withEmail().withStudent(),
              FakeUser.create().withEmail().withStudent().withTeacher());

      var users = fakeUsers.stream().map(FakeUser::toModel).toList();

      userRepository.saveAll(users);
      log.info("✓ {} users created", users.size());

      var students = fakeUsers.stream().map(FakeUser::getStudent).filter(Objects::nonNull).toList();
      userRepository.saveAllStudents(students);
      log.info("✓ {} students synced", students.size());

      var teachers = fakeUsers.stream().map(FakeUser::getTeacher).filter(Objects::nonNull).toList();
      userRepository.saveAllTeachers(teachers);
      log.info("✓ {} teachers synced", teachers.size());

      var externalUsers =
          users.stream()
              .map(
                  user ->
                      FakeExternalUser.of(
                              user,
                              user.isStudent() ? EUserCategory.STUDENT : EUserCategory.TEACHER)
                          .toModel())
              .toList();

      externalUserRepository.saveAll(externalUsers);
      log.info("✓ {} externalUsers created", externalUsers.size());

      var institutions =
          List.of(
              FakeInstitution.create().toModel(),
              FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel(),
              FakeInstitution.create()
                  .withEnabledFiled(Set.of(EPortfolioType.LIFE_PROJECT))
                  .toModel());

      institutionRepository.saveAll(institutions);
      log.info("✓ {} institutions created", institutions.size());

      var programs =
          institutions.stream().map(institution -> FakeProgram.of(institution).toModel()).toList();

      programRepository.saveAll(programs);
      log.info("✓ {} programs created", programs.size());

      var skillLevels =
          Set.of(FakeSkillLevel.create().withStatus(ESkillLevelStatus.VALIDATED).toModel());

      var skill = FakeSkill.of(skillLevels).toModel();

      var programProgresses =
          users.stream()
              .map(User::toStudent)
              .filter(Objects::nonNull)
              .map(
                  student ->
                      FakeProgramProgress.of(programs.getFirst(), student, Set.of(skill)).toModel())
              .toList();

      programProgressRepository.saveAll(programProgresses);
      programProgressRepository.flush();
      skillRepository.saveAll(List.of(skill));
      skillRepository.flush();
      log.info("✓ {} programProgresses created", programProgresses.size());

      var ams = FakeAMS.of(users.getFirst()).toModel();

      amsRepository.save(ams);
      log.info("✓ 1 ams created");

      var trackWithGroup =
          FakeTrack.of(users.getFirst())
              .withCreatedAt(Instant.now().minus(Duration.ofHours(3)))
              .toModel();
      var track2 =
          FakeTrack.of(users.getFirst())
              .isGroup()
              .withAMS(List.of(ams))
              .withCreatedAt(Instant.now().minus(Duration.ofHours(2)))
              .toModel();
      var track3 =
          FakeTrack.of(users.getFirst())
              .isGroup()
              .withCreatedAt(Instant.now().minus(Duration.ofHours(1)))
              .withSkillLevel(skillLevels.stream().toList())
              .withAMS(List.of(ams))
              .toModel();

      var track = FakeTrack.of(users.getFirst()).toModel();
      var tracks = List.of(track, trackWithGroup, track2, track3);

      skillLevelRepository.saveAll(skillLevels.stream().toList());
      trackRepository.saveAll(tracks);
      log.info("✓ {} tracks created", tracks.size());

      log.info("Seeding successfully finished");
    } else log.info("Seeder is disabled: seeding skipped");
  }
}
