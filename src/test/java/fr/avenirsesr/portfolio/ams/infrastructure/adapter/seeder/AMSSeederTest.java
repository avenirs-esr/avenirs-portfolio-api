package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository.AMSDatabaseRepository;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.seeder.DeclaredSkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.seeder.DeclaredSkillProgressSeeder;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.seeder.StudentProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.StudentSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

class AMSSeederTest extends ContainerConfigurationTest {

  @Autowired UserSeeder userSeeder;

  @Autowired StudentSeeder studentSeeder;

  @Autowired TraceSeeder traceSeeder;

  @Autowired SkillSeeder skillSeeder;

  @Autowired CohortSeeder cohortSeeder;

  @Autowired ProgramSeeder programSeeder;

  @Autowired TrainingPathSeeder trainingPathSeeder;

  @Autowired StudentProgressSeeder studentProgressSeeder;

  @Autowired InstitutionSeeder institutionSeeder;
  @Autowired private DeclaredSkillProgressSeeder declaredSkillProgressSeeder;
  @Autowired private DeclaredSkillSeeder declaredSkillSeeder;

  @Mock private AMSDatabaseRepository amsRepository;

  @InjectMocks private AMSSeeder amsSeeder;

  private static List<UserEntity> users;
  private static List<StudentEntity> students;
  private static List<SkillLevelProgressEntity> skillLevels;
  private static List<TraceEntity> traces;
  private static List<CohortEntity> cohorts;

  @BeforeAll
  void setUp() {

    // Seed les données comme dans SeederRunner
    var savedUsers = userSeeder.seed();
    var savedStudents = studentSeeder.seed(savedUsers);
    var declaredSkills = declaredSkillSeeder.seed();
    List<DeclaredSkillProgressEntity> declaredSkillProgresses =
        declaredSkillProgressSeeder.seed(savedStudents, declaredSkills);
    var savedInstitutions = institutionSeeder.seed();
    var savedPrograms = programSeeder.seed(savedInstitutions);
    var savedTraces = traceSeeder.seed(savedUsers, declaredSkillProgresses);
    var savedSkillLevels = skillSeeder.seed(savedPrograms);
    var savedTrainingPaths = trainingPathSeeder.seed(savedPrograms, savedSkillLevels);
    var savedStudentProgresses =
        studentProgressSeeder.seed(savedTrainingPaths, savedStudents, savedSkillLevels);
    var savedSkillLevelProgresses =
        savedStudentProgresses.stream().flatMap(s -> s.getSkillLevels().stream()).toList();
    var savedCohorts = cohortSeeder.seed(savedUsers, savedTrainingPaths);

    users = savedUsers;
    students = savedStudents;
    traces = savedTraces;
    skillLevels = savedSkillLevelProgresses;
    cohorts = savedCohorts;
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    BddLogger.given("an AMS seeder");
    BddLogger.when("users list is empty");
    List<StudentEntity> emptyUsers = List.of();

    BddLogger.then("it should throw IllegalArgumentException");
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> amsSeeder.seed(emptyUsers, skillLevels, traces, cohorts));
    assertTrue(exception.getMessage().contains("students cannot be empty"));
  }

  @Test
  void seed_shouldReturnAMSList_withCorrectSizeAndFields() {
    BddLogger.given("an AMS seeder");
    BddLogger.when("the seeder is called with correct arguments");
    List<AMSEntity> result = amsSeeder.seed(students, skillLevels, traces, cohorts);

    BddLogger.then(
        "it should return an AMS list with correct size and fields and call the amsRepository");
    assertNotNull(result);
    assertEquals(SeederConfig.AMS_NB, result.size());

    for (AMSEntity ams : result) {
      assertNotNull(ams.getStudent());
      assertNotNull(ams.getStatus());
      assertNotNull(ams.getTranslations());
      assertTrue(ams.getTranslations().stream().anyMatch(t -> t.getLanguage() != null));
    }
  }

  @Test
  void seed_shouldIncludeAllTranslations() {
    BddLogger.given("an AMS seeder");
    BddLogger.when("the seeder is called with correct arguments");
    List<AMSEntity> result = amsSeeder.seed(students, skillLevels, traces, cohorts);

    BddLogger.then("it should return an AMS list with all translations");
    for (AMSEntity ams : result) {
      assertTrue(
          ams.getTranslations().stream().anyMatch(t -> t.getLanguage().name().equals("ENGLISH")));
      assertTrue(
          ams.getTranslations().stream().anyMatch(t -> t.getLanguage().name().equals("SPANISH")));
    }
  }
}
