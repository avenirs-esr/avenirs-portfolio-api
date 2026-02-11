package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.seeder.ActivitySeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.AMSSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.CohortSeeder;
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
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.DeclaredExperienceSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.DeclaredProgramSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.seeder.DeclaredSkillProgressSeeder;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.StudentProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.ExternalUserSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.TeacherSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  private final DependencyChecker dependencyChecker;

  private final UserSeeder userSeeder;
  private final ExternalUserSeeder externalUserSeeder;
  private final StudentSeeder studentSeeder;
  private final TeacherSeeder teacherSeeder;
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

      var savedTeachers = teacherSeeder.seed(savedUsers);
      var savedStudents = studentSeeder.seed(savedUsers, savedSelfKnowledgeMandatoryCategories);

      declaredExperienceSeeder.seed(savedStudents);

      userPhotoSeeder.seed(savedStudents, savedTeachers);

      var savedDeclaredSkills = declaredSkillSeeder.seed();
      var savedStudentDeclaredSkills =
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
              savedStudentDeclaredSkills);

      amsSeeder.seed(savedStudents, savedSkillLevelProgresses, savedTraces, savedCohorts);

      selfKnowledgeElementSeeder.seed();

      declaredProgramSeeder.seed(savedStudents);

      activitySeeder.seed(savedUsers.getFirst());

      log.info("✔ Seeding successfully finished");
      seedingState.markCompleted();
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Seeding failed", e);
      throw e;
    }
  }
}
