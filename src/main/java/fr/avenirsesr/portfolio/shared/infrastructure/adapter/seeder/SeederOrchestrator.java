package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.ActivitySeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.AMSSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.CohortSeeder;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder.AssociationSeeder;
import fr.avenirsesr.portfolio.common.dependency.domain.port.input.DependencyChecker;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.configuration.SeedingState;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.DeclaredSkillSeeder;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.UserPhotoSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.SelfKnowledgeCategorySeeder;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.SelfKnowledgeElementSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.seeder.DeclaredActivitySeeder;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.DeclaredExperienceSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.DeclaredProgramSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.seeder.DeclaredSkillProgressSeeder;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.StudentProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.ExternalUserSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StaffSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeederOrchestrator {

  @Value("${seeder.dependencies-check:false}")
  private boolean dependenciesCheck;

  @Value("${avenirs.interoperability.actuator.health}")
  private String interoperabilityHealthUrl;

  @Value("${seeder.schema:dev}")
  private String schemaName;

  private final ReentrantLock lock = new ReentrantLock();
  private final JdbcTemplate jdbcTemplate;

  private final DependencyChecker dependencyChecker;

  private final UserSeeder userSeeder;
  private final ExternalUserSeeder externalUserSeeder;
  private final StudentSeeder studentSeeder;
  private final StaffSeeder staffSeeder;
  private final UserPhotoSeeder userPhotoSeeder;

  private final SelfKnowledgeCategorySeeder selfKnowledgeCategorySeeder;
  private final SelfKnowledgeElementSeeder selfKnowledgeElementSeeder;

  private final DeclaredExperienceSeeder declaredExperienceSeeder;
  private final DeclaredSkillSeeder declaredSkillSeeder;
  private final DeclaredSkillProgressSeeder declaredSkillProgressSeeder;
  private final DeclaredProgramSeeder declaredProgramSeeder;

  private final InstitutionSeeder institutionSeeder;
  private final ProgramSeeder programSeeder;
  private final TrainingPathSeeder trainingPathSeeder;
  private final SkillSeeder skillSeeder;
  private final StudentProgressSeeder studentProgressSeeder;

  private final CohortSeeder cohortSeeder;
  private final TraceSeeder traceSeeder;
  private final AMSSeeder amsSeeder;
  private final ActivitySeeder activitySeeder;
  private final DeclaredActivitySeeder declaredActivitySeeder;
  private final AssociationSeeder associationSeeder;

  private final SeedingState seedingState;

  @Transactional()
  public void seedAll() {
    try {
      log.info("Seeding enabled and starting...");

      if (dependenciesCheck) {
        dependencyChecker.checkAndWait("Interoperability", interoperabilityHealthUrl);
      }

      var savedSelfKnowledgeMandatoryCategories = selfKnowledgeCategorySeeder.seed();

      var savedUsers = userSeeder.seed();
      externalUserSeeder.seed(savedUsers);

      var savedStaffs = staffSeeder.seed(savedUsers);
      var savedStudents = studentSeeder.seed(savedUsers, savedSelfKnowledgeMandatoryCategories);

      var savedDeclaredExperiences = declaredExperienceSeeder.seed(savedStudents);

      userPhotoSeeder.seed(savedStudents, savedStaffs);

      var savedDeclaredSkills = declaredSkillSeeder.seed();
      var savedDeclaredSkillProgresses =
          declaredSkillProgressSeeder.seed(savedStudents, savedDeclaredSkills);

      var savedInstitutions = institutionSeeder.seed();
      var savedPrograms = programSeeder.seed(savedInstitutions);

      var savedSkillLevels = skillSeeder.seed(savedPrograms);
      var savedSkills =
          savedSkillLevels.stream().map(SkillLevelEntity::getSkill).distinct().toList();

      var savedTrainingPaths = trainingPathSeeder.seed(savedPrograms, savedSkillLevels);

      var savedStudentProgresses =
          studentProgressSeeder.seed(savedTrainingPaths, savedStudents, savedSkillLevels);

      var savedSkillLevelProgresses =
          savedStudentProgresses.stream().flatMap(s -> s.getSkillLevels().stream()).toList();

      var savedCohorts = cohortSeeder.seed(savedUsers, savedTrainingPaths);

      var savedTraces =
          traceSeeder.seed(
              savedStudents.stream().map(StudentEntity::getUser).toList(),
              savedDeclaredSkillProgresses);

      amsSeeder.seed(savedStudents, savedSkillLevelProgresses, savedTraces, savedCohorts);

      selfKnowledgeElementSeeder.seed();

      declaredProgramSeeder.seed(savedStudents);

      var savedActivities = activitySeeder.seed(savedUsers.getFirst());
      var declaredActivities = declaredActivitySeeder.seed(savedStudents, savedActivities);
      associationSeeder.seed(
          declaredActivities, savedTraces, savedDeclaredSkillProgresses, savedDeclaredExperiences);

      log.info("✔ Seeding successfully finished");
      seedingState.markCompleted();
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Seeding failed", e);
      throw e;
    }
  }

  @Transactional()
  public void clearAll() {
    List<String> tables =
        jdbcTemplate.queryForList(
            """
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = ?
              AND tablename NOT IN ('databasechangelog', 'databasechangeloglock')
            """,
            String.class,
            schemaName);

    if (tables.isEmpty()) {
      log.warn("No tables found in schema '{}'", schemaName);
      return;
    }

    String joined =
        tables.stream()
            .map(t -> "\"" + schemaName + "\".\"" + t + "\"")
            .reduce((a, b) -> a + ", " + b)
            .orElseThrow();

    String sql = "TRUNCATE TABLE " + joined + " RESTART IDENTITY CASCADE";

    log.warn("Resetting DB: {}", sql);
    jdbcTemplate.execute(sql);
  }

  public void resetAndSeed() {
    if (!lock.tryLock()) {
      throw new IllegalStateException("Seeding already running");
    }
    try {
      clearAll();
      seedAll();
    } finally {
      lock.unlock();
    }
  }
}
