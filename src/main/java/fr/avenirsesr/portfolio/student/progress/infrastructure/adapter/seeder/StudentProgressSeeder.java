package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.fake.FakeStudentProgress;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
    log.info("Seeding student progress...");

    if (savedTrainingPaths.isEmpty() || savedUsers.isEmpty() || savedSkillLevels.isEmpty()) {
      log.warn("No training paths, users, or skills available for seeding student progress.");
      return List.of();
    }

    AtomicInteger index = new AtomicInteger(0);

    Map<UUID, List<SkillLevelEntity>> skillsByTrainingPath =
        savedTrainingPaths.stream()
            .collect(
                Collectors.toMap(
                    TrainingPathEntity::getId,
                    trainingPath ->
                        trainingPath.getSkillLevels().stream()
                            .map(
                                skillLevelEntity ->
                                    savedSkillLevels.stream()
                                        .filter(
                                            skillLevel ->
                                                skillLevel.getId().equals(skillLevelEntity.getId()))
                                        .findFirst()
                                        .orElseThrow(
                                            () ->
                                                new IllegalStateException(
                                                    "Skill level not found: "
                                                        + skillLevelEntity.getId())))
                            .toList()));

    List<StudentProgressEntity> studentProgressEntities =
        savedUsers.stream()
            .flatMap(
                user -> {
                  TrainingPathEntity selectedTrainingPath =
                      savedTrainingPaths.get(
                          index.getAndUpdate(i -> i < savedTrainingPaths.size() - 1 ? i + 1 : 0));

                  List<SkillLevelEntity> trainingPathSkills =
                      skillsByTrainingPath.getOrDefault(selectedTrainingPath.getId(), List.of());

                  return trainingPathSkills.stream()
                      .map(
                          skillLevel ->
                              createFakeStudentProgress(user, selectedTrainingPath, skillLevel));
                })
            .toList();

    studentProgressRepository.saveAllEntities(studentProgressEntities);
    log.info("✔ {} studentProgresses created", studentProgressEntities.size());
    return studentProgressEntities;
  }

  private StudentProgressEntity createFakeStudentProgress(
      UserEntity student, TrainingPathEntity trainingPath, SkillLevelEntity skillLevel) {
    return FakeStudentProgress.of(student, trainingPath, skillLevel).toEntity();
  }
}
