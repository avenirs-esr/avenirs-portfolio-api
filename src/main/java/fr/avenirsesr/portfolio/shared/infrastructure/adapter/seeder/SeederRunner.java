package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.AdditionalSkillSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.AMSSeeder;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.CohortSeeder;
import fr.avenirsesr.portfolio.common.dependency.domain.port.input.DependencyChecker;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.TraceAttachmentSeeder;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.seeder.UserPhotoSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.SelfKnowledgeCategorySeeder;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.SelfKnowledgeElementSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.seeder.DeclaredProgramSeeder;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.seeder.DeclaredExperienceSeeder;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.AdditionalSkillProgressSeeder;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.StudentProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
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
  private final StudentSeeder studentSeeder;
  private final TeacherSeeder teacherSeeder;
  private final UserPhotoSeeder userPhotoSeeder;
  private final CohortSeeder cohortSeeder;
  private final AMSSeeder amsSeeder;
  private final TraceSeeder traceSeeder;
  private final TraceAttachmentSeeder traceAttachmentSeeder;
  private final InstitutionSeeder institutionSeeder;
  private final ProgramSeeder programSeeder;
  private final TrainingPathSeeder trainingPathSeeder;
  private final StudentProgressSeeder studentProgressSeeder;
  private final SkillSeeder skillSeeder;
  private final AdditionalSkillSeeder additionalSkillSeeder;
  private final AdditionalSkillProgressSeeder additionalSkillProgressSeeder;
  private final SelfKnowledgeElementSeeder selfKnowledgeElementSeeder;
  private final SelfKnowledgeCategorySeeder selfKnowledgeCategorySeeder;
  private final DeclaredExperienceSeeder declaredExperienceSeeder;
  private final DeclaredProgramSeeder declaredProgramSeeder;

  @Value("${seeder.enabled:false}")
  private boolean seedEnabled;

  @Value("${seeder.source:FAKER}")
  private String seederSource;

  @Value("${avenirs.interoperability.actuator.health}")
  private String interoperabilityHealthUrl;

  public SeederRunner(
      DependencyChecker dependencyChecker,
      UserRepository userRepository,
      UserSeeder userSeeder,
      StudentSeeder studentSeeder,
      TeacherSeeder teacherSeeder,
      UserPhotoSeeder userPhotoSeeder,
      CohortSeeder cohortSeeder,
      AMSSeeder amsSeeder,
      TraceSeeder traceSeeder,
      TraceAttachmentSeeder traceAttachmentSeeder,
      InstitutionSeeder institutionSeeder,
      ProgramSeeder programSeeder,
      TrainingPathSeeder trainingPathSeeder,
      StudentProgressSeeder studentProgressSeeder,
      SkillSeeder skillSeeder,
      AdditionalSkillSeeder additionalSkillSeeder,
      AdditionalSkillProgressSeeder additionalSkillProgressSeeder,
      SelfKnowledgeElementSeeder selfKnowledgeElementSeeder,
      SelfKnowledgeCategorySeeder selfKnowledgeCategorySeeder,
      DeclaredExperienceSeeder declaredExperienceSeeder,
      DeclaredProgramSeeder declaredProgramSeeder) {

    this.dependencyChecker = dependencyChecker;
    this.userRepository = userRepository;
    this.userPhotoSeeder = userPhotoSeeder;
    this.studentSeeder = studentSeeder;
    this.teacherSeeder = teacherSeeder;
    this.cohortSeeder = cohortSeeder;
    this.amsSeeder = amsSeeder;
    this.traceSeeder = traceSeeder;
    this.userSeeder = userSeeder;
    this.traceAttachmentSeeder = traceAttachmentSeeder;
    this.institutionSeeder = institutionSeeder;
    this.programSeeder = programSeeder;
    this.trainingPathSeeder = trainingPathSeeder;
    this.studentProgressSeeder = studentProgressSeeder;
    this.skillSeeder = skillSeeder;
    this.additionalSkillSeeder = additionalSkillSeeder;
    this.additionalSkillProgressSeeder = additionalSkillProgressSeeder;
    this.selfKnowledgeElementSeeder = selfKnowledgeElementSeeder;
    this.selfKnowledgeCategorySeeder = selfKnowledgeCategorySeeder;
    this.declaredExperienceSeeder = declaredExperienceSeeder;
    this.declaredProgramSeeder = declaredProgramSeeder;
  }

  @Override
  public void run(String... args) {
    long userCont = userRepository.countAll();

    if (seedEnabled && userCont == 0) {
      log.info("Seeding enabled and starting...");

      if (!"FAKER".equalsIgnoreCase(seederSource)) {
        dependencyChecker.checkAndWait("Interoperability", interoperabilityHealthUrl);
      }

      var savedSelfKnowledgeMandatoryCategories = selfKnowledgeCategorySeeder.seed();
      var savedUsers = userSeeder.seed();
      var savedTeachers = teacherSeeder.seed(savedUsers);
      var savedStudents = studentSeeder.seed(savedUsers, savedSelfKnowledgeMandatoryCategories);
      declaredExperienceSeeder.seed(savedStudents);
      var savedUserPhotos = userPhotoSeeder.seed(savedStudents, savedTeachers);
      var savedAdditionalSkills = additionalSkillSeeder.seed();
      var savedStudentAdditionalSkills =
          additionalSkillProgressSeeder.seed(savedStudents, savedAdditionalSkills);
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
      var savedTraces = traceSeeder.seed(savedUsers, savedStudentAdditionalSkills);
      var savedTracesAttachment = traceAttachmentSeeder.seed(savedTraces);
      var savedAmses =
          amsSeeder.seed(savedStudents, savedSkillLevelProgresses, savedTraces, savedCohorts);
      var savedSelfKnowledgeElements = selfKnowledgeElementSeeder.seed(savedStudents);
      var savedDeclaredProgramSeeder = declaredProgramSeeder.seed(savedStudents);

      log.info("✔ Seeding successfully finished");
    } else log.info("{} users found. Seeder is disabled: seeding skipped", userCont);
  }
}
