package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.common.validation.infrastructure.adapter.utils.ValidationUtils;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.repository.StudentProgressDatabaseRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.fake.FakeSkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.fake.FakeStudentProgress;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentProgressSeeder {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(StudentProgressSeeder.class, SharedDataGenerator.class);

  private final StudentProgressDatabaseRepository studentProgressRepository;

  private static StudentProgressEntity buildStudentProgressEntity(
      StudentEntity student, LocalDate startDate, List<TrainingPathEntity> savedTrainingPaths) {
    TrainingPathEntity selectedTrainingPath =
        dataGenerator.with("selectedTrainingPath").pickIn(savedTrainingPaths);

    var skillLevelProgressEntities =
        selectedTrainingPath.getSkillLevels().stream()
            .map(
                level ->
                    FakeSkillLevelProgress.create(student, level)
                        .withStatus(
                            dataGenerator.with("ESkillLevelStatus").pickIn(ESkillLevelStatus.class))
                        .toEntity())
            .toList();

    return FakeStudentProgress.of(student, selectedTrainingPath, skillLevelProgressEntities)
        .withStartDate(startDate)
        .toEntity();
  }

  @Transactional
  public List<StudentProgressEntity> seed(
      List<TrainingPathEntity> savedTrainingPaths,
      List<StudentEntity> savedStudents,
      List<SkillLevelEntity> savedSkillLevels) {
    ValidationUtils.requireNonEmpty(savedTrainingPaths, "training paths cannot be empty");
    ValidationUtils.requireNonEmpty(savedStudents, "users cannot be empty");
    ValidationUtils.requireNonEmpty(savedSkillLevels, "skill levels cannot be empty");
    log.info("Seeding student progress...");

    List<StudentProgressEntity> studentProgressEntities = new ArrayList<>();

    var yesterday = LocalDate.now().minusDays(1);
    savedStudents.forEach(
        student -> {
          studentProgressEntities.add(
              buildStudentProgressEntity(student, yesterday, savedTrainingPaths));
          studentProgressEntities.add(
              buildStudentProgressEntity(student, yesterday.minusYears(1), savedTrainingPaths));
          studentProgressEntities.add(
              buildStudentProgressEntity(student, yesterday.plusYears(1), savedTrainingPaths));
        });

    studentProgressRepository.saveAllEntities(studentProgressEntities);
    log.info("✔ {} studentProgresses created", studentProgressEntities.size());
    return studentProgressEntities;
  }
}
