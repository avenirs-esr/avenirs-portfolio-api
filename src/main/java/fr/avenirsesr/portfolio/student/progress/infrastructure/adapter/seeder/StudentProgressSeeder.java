package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake.FakeStudentProgress;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentProgressSeeder {

  private final StudentProgressDatabaseRepository studentProgressRepository;

  public List<StudentProgressEntity> seed(
      List<TrainingPathEntity> savedTrainingPaths,
      List<UserEntity> savedUsers,
      List<SkillLevelEntity> savedSkillLevels) {
    ValidationUtils.requireNonEmpty(savedTrainingPaths, "training paths cannot be empty");
    ValidationUtils.requireNonEmpty(savedUsers, "users cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkillLevels, "skill levels cannot be empty");
    log.info("Seeding student progress...");

    AtomicInteger index = new AtomicInteger(0);

    List<StudentProgressEntity> studentProgressEntities =
        savedUsers.stream()
            .flatMap(
                user -> {
                  TrainingPathEntity selectedTrainingPath =
                      savedTrainingPaths.get(
                          index.getAndUpdate(i -> i < savedTrainingPaths.size() - 1 ? i + 1 : 0));

                  Set<SkillLevelEntity> trainingPathSkills = selectedTrainingPath.getSkillLevels();

                  return trainingPathSkills.stream()
                      .map(
                          skillLevel ->
                              FakeStudentProgress.of(user, selectedTrainingPath, skillLevel)
                                  .toEntity());
                })
            .toList();

    studentProgressRepository.saveAllEntities(studentProgressEntities);
    log.info("✔ {} studentProgresses created", studentProgressEntities.size());
    return studentProgressEntities;
  }
}
