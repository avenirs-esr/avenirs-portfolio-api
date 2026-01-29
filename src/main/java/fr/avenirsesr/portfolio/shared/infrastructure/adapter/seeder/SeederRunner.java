package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

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
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.ExternalUserSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.TeacherSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SeederRunner implements CommandLineRunner {
  private final DependencyChecker dependencyChecker;
  private final UserRepository userRepository;
  private final UserSeeder userSeeder;
  private final ExternalUserSeeder externalUserSeeder;
  private final StudentSeeder studentSeeder;
  private final TeacherSeeder teacherSeeder;
  private final UserPhotoSeeder userPhotoSeeder;
  private final CohortSeeder cohortSeeder;
  private final AMSSeeder amsSeeder;
  private final TraceSeeder traceSeeder;
  private final InstitutionSeeder institutionSeeder;
  private final ProgramSeeder programSeeder;
  private final TrainingPathSeeder trainingPathSeeder;
  private final StudentProgressSeeder studentProgressSeeder;
  private final SkillSeeder skillSeeder;
  private final DeclaredSkillSeeder declaredSkillSeeder;
  private final DeclaredSkillProgressSeeder declaredSkillProgressSeeder;
  private final SelfKnowledgeElementSeeder selfKnowledgeElementSeeder;
  private final SelfKnowledgeCategorySeeder selfKnowledgeCategorySeeder;
  private final DeclaredExperienceSeeder declaredExperienceSeeder;
  private final DeclaredProgramSeeder declaredProgramSeeder;
  private final SeedingState seedingState;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  @Value("${seeder.source:FAKER}")
  private String seederSource;

  @Value("${dependencies-check:false}")
  private boolean dependenciesCheck;

  @Value("${avenirs.interoperability.actuator.health}")
  private String interoperabilityHealthUrl;

  public SeederRunner(
      DependencyChecker dependencyChecker,
      UserRepository userRepository,
      UserSeeder userSeeder,
      ExternalUserSeeder externalUserSeeder,
      StudentSeeder studentSeeder,
      TeacherSeeder teacherSeeder,
      UserPhotoSeeder userPhotoSeeder,
      CohortSeeder cohortSeeder,
      AMSSeeder amsSeeder,
      TraceSeeder traceSeeder,
      InstitutionSeeder institutionSeeder,
      ProgramSeeder programSeeder,
      TrainingPathSeeder trainingPathSeeder,
      StudentProgressSeeder studentProgressSeeder,
      SkillSeeder skillSeeder,
      DeclaredSkillSeeder declaredSkillSeeder,
      DeclaredSkillProgressSeeder declaredSkillProgressSeeder,
      SelfKnowledgeElementSeeder selfKnowledgeElementSeeder,
      SelfKnowledgeCategorySeeder selfKnowledgeCategorySeeder,
      DeclaredExperienceSeeder declaredExperienceSeeder,
      DeclaredProgramSeeder declaredProgramSeeder,
      SeedingState seedingState) {

    this.dependencyChecker = dependencyChecker;
    this.userRepository = userRepository;
    this.externalUserSeeder = externalUserSeeder;
    this.userPhotoSeeder = userPhotoSeeder;
    this.studentSeeder = studentSeeder;
    this.teacherSeeder = teacherSeeder;
    this.cohortSeeder = cohortSeeder;
    this.amsSeeder = amsSeeder;
    this.traceSeeder = traceSeeder;
    this.userSeeder = userSeeder;
    this.institutionSeeder = institutionSeeder;
    this.programSeeder = programSeeder;
    this.trainingPathSeeder = trainingPathSeeder;
    this.studentProgressSeeder = studentProgressSeeder;
    this.skillSeeder = skillSeeder;
    this.declaredSkillSeeder = declaredSkillSeeder;
    this.declaredSkillProgressSeeder = declaredSkillProgressSeeder;
    this.selfKnowledgeElementSeeder = selfKnowledgeElementSeeder;
    this.selfKnowledgeCategorySeeder = selfKnowledgeCategorySeeder;
    this.declaredExperienceSeeder = declaredExperienceSeeder;
    this.declaredProgramSeeder = declaredProgramSeeder;
    this.seedingState = seedingState;
  }

  @Override
  public void run(String... args) {
    try {
      long userCont = userRepository.countAll();
      if (seedEnabled && userCont == 0) {
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
        var savedUserPhotos = userPhotoSeeder.seed(savedStudents, savedTeachers);
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
        var savedTraces = traceSeeder.seed(savedUsers, savedStudentDeclaredSkills);
        var savedAmses =
            amsSeeder.seed(savedStudents, savedSkillLevelProgresses, savedTraces, savedCohorts);
        var savedSelfKnowledgeElements = selfKnowledgeElementSeeder.seed(savedStudents);
        var savedDeclaredProgramSeeder = declaredProgramSeeder.seed(savedStudents);

        log.info("✔ Seeding successfully finished");
      } else {
        log.info("{} users found. Seeder is disabled: seeding skipped", userCont);
      }
      seedingState.markCompleted();
    } catch (Exception e) {
      seedingState.markFailed(e);
      log.error("✘ Seeding failed", e);
      throw e;
    }
  }

  public boolean isDependenciesCheck() {
    return dependenciesCheck;
  }

  public void setDependenciesCheck(boolean dependenciesCheck) {
    this.dependenciesCheck = dependenciesCheck;
  }
}
