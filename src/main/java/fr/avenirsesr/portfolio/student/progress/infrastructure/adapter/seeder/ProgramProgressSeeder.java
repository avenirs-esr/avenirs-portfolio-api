package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.ProgramProgressDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeProgramProgress;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgramProgressSeeder {
  private static final Faker faker = new FakerProvider().call();

  private final ProgramProgressDatabaseRepository programProgressRepository;
  private final SkillDatabaseRepository skillDatabaseRepository;

  private FakeProgramProgress createFakeProgramProgress(
      ProgramEntity program, UserEntity student, Set<SkillEntity> skills) {
    return FakeProgramProgress.of(program, student, skills);
  }

  private Set<SkillEntity> getRandomSkills(List<SkillEntity> savedSkills) {
    List<SkillEntity> skills = new ArrayList<>(savedSkills);
    Collections.shuffle(skills);

    return new HashSet<>(skills.subList(0, SeederConfig.SKILL_BY_PROGRAM));
  }

  private List<ProgramProgressEntity> generateFakeProgramProgressEntities(
      List<ProgramEntity> savedPrograms,
      List<UserEntity> savedUsers,
      List<SkillEntity> savedSkills) {
    List<ProgramProgressEntity> programProgressEntities = new ArrayList<>();
    var students =
        savedUsers.stream()
            .filter(
                userEntity ->
                    userEntity.getStudent().isPresent() && userEntity.getStudent().get().isActive())
            .toList();

    for (ProgramEntity programEntity : savedPrograms) {
      for (int i = 0; i < SeederConfig.PROGRAM_PROGRESS_BY_PROGRAM; i++) {
        for (int j = 0;
            j < faker.random().nextInt(SeederConfig.PROGRAM_PROGRESS_NB_STUDENT_MAX);
            j++) {
          programProgressEntities.add(
              createFakeProgramProgress(
                      programEntity,
                      students.get(faker.random().nextInt(students.size())),
                      getRandomSkills(savedSkills))
                  .toEntity());
        }
      }
    }

    return programProgressEntities;
  }

  public List<ProgramProgressEntity> seed(
      List<ProgramEntity> savedPrograms,
      List<UserEntity> savedUsers,
      List<SkillEntity> savedSkills) {
    ValidationUtils.requireNonEmpty(savedPrograms, "programs cannot be empty");
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkills, "skills cannot be empty");
    log.info("Seeding program progress...");

    List<ProgramProgressEntity> programProgressEntities =
        generateFakeProgramProgressEntities(savedPrograms, savedUsers, savedSkills);

    programProgressRepository.saveAllEntities(programProgressEntities);
    programProgressEntities.forEach(
        programProgress -> {
          programProgress
              .getSkills()
              .forEach(
                  skill -> {
                    skill.setProgramProgress(programProgress);
                  });
        });
    skillDatabaseRepository.saveAllEntities(
        programProgressEntities.stream().flatMap(p -> p.getSkills().stream()).toList());

    log.info("✔ {} programProgresses created", programProgressEntities.size());
    return programProgressEntities;
  }
}
