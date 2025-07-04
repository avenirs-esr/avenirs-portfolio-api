package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.ProgramEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.SkillDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.TrainingPathDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake.FakeTrainingPath;
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
public class TrainingPathSeeder {
  private static final Faker faker = new FakerProvider().call();

  private final TrainingPathDatabaseRepository trainingPathRepository;
  private final SkillDatabaseRepository skillDatabaseRepository;

  private FakeTrainingPath createFakeTrainingPath(
      ProgramEntity program, Set<SkillLevelEntity> skillLevels) {
    return FakeTrainingPath.of(program, skillLevels);
  }

  private Set<SkillLevelEntity> getRandomSkills(List<SkillLevelEntity> savedSkills) {
    List<SkillLevelEntity> skills = new ArrayList<>(savedSkills);
    Collections.shuffle(skills);

    return new HashSet<>(skills.subList(0, SeederConfig.SKILL_BY_PROGRAM));
  }

  private List<TrainingPathEntity> generateFakeTrainingPathEntities(
      List<ProgramEntity> savedPrograms,
      List<UserEntity> savedUsers,
      List<SkillLevelEntity> savedSkills) {
    List<TrainingPathEntity> trainingPathEntities = new ArrayList<>();

    for (ProgramEntity programEntity : savedPrograms) {
      for (int i = 0; i < SeederConfig.PROGRAM_PROGRESS_BY_PROGRAM; i++) {
        for (int j = 0;
            j < faker.random().nextInt(SeederConfig.PROGRAM_PROGRESS_NB_STUDENT_MAX);
            j++) {
          trainingPathEntities.add(
              createFakeTrainingPath(programEntity, getRandomSkills(savedSkills)).toEntity());
        }
      }
    }

    return trainingPathEntities;
  }

  public List<TrainingPathEntity> seed(
      List<ProgramEntity> savedPrograms,
      List<UserEntity> savedUsers,
      List<SkillLevelEntity> savedSkills) {
    ValidationUtils.requireNonEmpty(savedPrograms, "programs cannot be empty");
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkills, "skills cannot be empty");
    log.info("Seeding training path...");

    List<TrainingPathEntity> trainingPathEntities =
        generateFakeTrainingPathEntities(savedPrograms, savedUsers, savedSkills);

    trainingPathRepository.saveAllEntities(trainingPathEntities);

    log.info("✔ {} trainingPathes created", trainingPathEntities.size());
    return trainingPathEntities;
  }
}
