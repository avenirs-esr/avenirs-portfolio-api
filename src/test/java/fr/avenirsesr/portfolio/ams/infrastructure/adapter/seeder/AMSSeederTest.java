package fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository.AMSDatabaseRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.InstitutionSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.ProgramSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.SkillSeeder;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.TrainingPathSeeder;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederConfig;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.seeder.StudentProgressSeeder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.TraceSeeder;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.UserSeeder;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AMSSeederTest {

  @Mock private AMSDatabaseRepository amsRepository;

  @InjectMocks private AMSSeeder amsSeeder;

  private static List<UserEntity> users;
  private static List<SkillLevelProgressEntity> skillLevels;
  private static List<TraceEntity> traces;
  private static List<CohortEntity> cohorts;

  @BeforeAll
  static void setUp(
      @Autowired UserSeeder userSeeder,
      @Autowired TraceSeeder traceSeeder,
      @Autowired SkillSeeder skillSeeder,
      @Autowired CohortSeeder cohortSeeder,
      @Autowired ProgramSeeder programSeeder,
      @Autowired TrainingPathSeeder trainingPathSeeder,
      @Autowired StudentProgressSeeder studentProgressSeeder,
      @Autowired InstitutionSeeder institutionSeeder) {

    // Seed les données comme dans SeederRunner
    var savedUsers = userSeeder.seed();
    var savedInstitutions = institutionSeeder.seed();
    var savedPrograms = programSeeder.seed(savedInstitutions);
    var savedTraces = traceSeeder.seed(savedUsers);
    var savedSkillLevels = skillSeeder.seed(savedPrograms);
    var savedTrainingPaths = trainingPathSeeder.seed(savedPrograms, savedSkillLevels);
    var savedStudents = savedUsers.stream().filter(u -> u.getStudent().isPresent()).toList();
    var savedStudentProgresses =
        studentProgressSeeder.seed(savedTrainingPaths, savedStudents, savedSkillLevels);
    var savedSkillLevelProgresses =
        savedStudentProgresses.stream().flatMap(s -> s.getSkillLevels().stream()).toList();
    var savedCohorts = cohortSeeder.seed(savedUsers, savedTrainingPaths);

    users = savedUsers;
    traces = savedTraces;
    skillLevels = savedSkillLevelProgresses;
    cohorts = savedCohorts;
  }

  @Test
  void seed_shouldThrowException_whenUsersEmpty() {
    List<UserEntity> emptyUsers = List.of();
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> amsSeeder.seed(emptyUsers, skillLevels, traces, cohorts));
    assertTrue(exception.getMessage().contains("users cannot be empty"));
  }

  @Test
  void seed_shouldReturnAMSList_withCorrectSizeAndFields() {
    List<AMSEntity> result = amsSeeder.seed(users, skillLevels, traces, cohorts);

    assertNotNull(result);
    assertEquals(SeederConfig.AMS_NB, result.size());

    for (AMSEntity ams : result) {
      assertNotNull(ams.getUser());
      assertNotNull(ams.getSkillLevels());
      assertNotNull(ams.getTraces());
      assertNotNull(ams.getCohorts());
      assertFalse(ams.getSkillLevels().isEmpty());
      if (SeederConfig.NB_TRACES_MIN_PER_AMS > 0) assertFalse(ams.getTraces().isEmpty());
      if (SeederConfig.NB_COHORTS_MIN_PER_AMS > 0) assertFalse(ams.getCohorts().isEmpty());
      assertNotNull(ams.getStatus());
      assertNotNull(ams.getTranslations());
      assertTrue(ams.getTranslations().stream().anyMatch(t -> t.getLanguage() != null));
    }

    // Vérifie que le repository a bien été appelé
    verify(amsRepository, times(1)).saveAllEntities(result);
  }

  @Test
  void seed_shouldIncludeAllTranslations() {
    List<AMSEntity> result = amsSeeder.seed(users, skillLevels, traces, cohorts);

    for (AMSEntity ams : result) {
      assertTrue(
          ams.getTranslations().stream().anyMatch(t -> t.getLanguage().name().equals("ENGLISH")));
      assertTrue(
          ams.getTranslations().stream().anyMatch(t -> t.getLanguage().name().equals("SPANISH")));
    }
  }
}
