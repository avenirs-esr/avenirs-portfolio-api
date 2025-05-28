package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

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
    long userCont = userRepository.countAll();

    if (seedEnabled) {
      var fakeUsers =new ArrayList<FakeUser>();
      fakeUsers.add(FakeUser.create().withEmail().withStudent());
      fakeUsers.add(FakeUser.create().withEmail().withStudent().withTeacher());
      IntStream.range(0, 10)
              .mapToObj(i -> FakeUser.create().withStudent().withStudent())
              .forEach(fakeUsers::add);

      var users = fakeUsers.stream().map(FakeUser::toModel).toList();

      var externalUsers =
          users.stream()
              .map(
                  user ->
                      FakeExternalUser.of(
                              user,
                              user.isStudent() ? EUserCategory.STUDENT : EUserCategory.TEACHER)
                          .toModel())
              .toList();

      var institutions =
          List.of(
              FakeInstitution.create().toModel(),
              FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel(),
              FakeInstitution.create()
                  .withEnabledFiled(Set.of(EPortfolioType.LIFE_PROJECT))
                  .toModel());

      var programs =
          institutions.stream().map(institution -> FakeProgram.of(institution).toModel()).toList();

      var programProgresses =
          users.stream()
              .map(User::toStudent)
              .filter(Objects::nonNull)
              .map(
                  student -> {
                    var skills =
                        Set.of(
                            FakeSkill.of(
                                    Set.of(
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.VALIDATED)
                                            .toModel(),
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.FAILED)
                                            .toModel(),
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.UNDER_REVIEW)
                                            .toModel()))
                                .toModel(),
                            FakeSkill.of(
                                    Set.of(
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.VALIDATED)
                                            .toModel(),
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.VALIDATED)
                                            .toModel(),
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
                                            .toModel()))
                                .toModel(),
                            FakeSkill.of(
                                    Set.of(
                                        FakeSkillLevel.create()
                                            .withStatus(ESkillLevelStatus.VALIDATED)
                                            .toModel(),
                                        FakeSkillLevel.create().toModel()))
                                .toModel());

                    return FakeProgramProgress.of(programs.getFirst(), student, skills).toModel();
                  })
              .toList();

      var track = FakeTrack.of(users.getFirst()).toModel();
      var ams = FakeAMS.of(users.getFirst()).toModel();

      userRepository.saveAll(users);
      log.info("✓ {} users created", users.size());

      var students = fakeUsers.stream().map(FakeUser::getStudent).filter(Objects::nonNull).toList();
      userRepository.saveAllStudents(students);
      log.info("✓ {} students synced", students.size());

      var teachers = fakeUsers.stream().map(FakeUser::getTeacher).filter(Objects::nonNull).toList();
      userRepository.saveAllTeachers(teachers);
      log.info("✓ {} teachers synced", teachers.size());

      externalUserRepository.saveAll(externalUsers);
      log.info("✓ {} externalUsers created", externalUsers.size());

      institutionRepository.saveAll(institutions);
      log.info("✓ {} institutions created", institutions.size());

      programRepository.saveAll(programs);
      log.info("✓ {} programs created", programs.size());

      var skillLevels =
          programProgresses.stream()
              .flatMap(programProgress -> programProgress.getSkills().stream())
              .flatMap(skills -> skills.getSkillLevels().stream())
              .toList();

      skillLevelRepository.saveAll(skillLevels);
      log.info("✓ {} skillLevels created", skillLevels.size());

      var skills =
          programProgresses.stream()
              .flatMap(programProgress -> programProgress.getSkills().stream())
              .toList();

      skillRepository.saveAll(skills);
      log.info("✓ {} skills created", skills.size());

      programProgressRepository.saveAll(programProgresses);
      log.info("✓ {} programProgresses created", programProgresses.size());

      trackRepository.save(track);
      log.info("✓ 1 track created");

      amsRepository.save(ams);
      log.info("✓ 1 ams created");

      log.info("Seeding successfully finished");

    } else log.info("{} users found. Seeder is disabled: seeding skipped", userCont);
  }
}
