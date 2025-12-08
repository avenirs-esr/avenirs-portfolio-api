package fr.avenirsesr.portfolio.program.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class InstitutionServiceImplTest {
  @Mock private StudentProgressRepository studentProgressRepository;
  @Mock private LoggedInUserService loggedInUserService;

  @InjectMocks private InstitutionServiceImpl institutionService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
  }

  @Test
  void shouldReturnTrueWhenInstitutionHasEnabledNavigationField() {
    BddLogger.given(
        "an InstitutionServiceImpl service and checking if the navigation is enabled for student");
    TrainingPath apcTrainingPath = TrainingPathFixture.createWithAPC().toModel();
    TrainingPath lifeProjectTrainingPath = TrainingPathFixture.createWithoutAPC().toModel();
    StudentProgress studentProgressAPC =
        StudentProgressFixture.create()
            .withTrainingPath(apcTrainingPath)
            .withStudent(student)
            .toModel();
    StudentProgress studentProgressLifeProject =
        StudentProgressFixture.create()
            .withTrainingPath(lifeProjectTrainingPath)
            .withStudent(student)
            .toModel();

    when(studentProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(studentProgressAPC, studentProgressLifeProject));

    BddLogger.when("at least one institution has enabled navigation field");
    boolean result = institutionService.isNavigationEnabledFor(EPortfolioType.APC);

    BddLogger.when("it should return true");
    assertTrue(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenNoInstitutionHasEnabledNavigationField() {
    BddLogger.given(
        "an InstitutionServiceImpl service and checking if the navigation is enabled for student");
    TrainingPath apcTrainingPath = TrainingPathFixture.createWithAPC().toModel();
    TrainingPath apcTrainingPath2 = TrainingPathFixture.createWithAPC().toModel();
    StudentProgress studentProgressAPC =
        StudentProgressFixture.create()
            .withTrainingPath(apcTrainingPath)
            .withStudent(student)
            .toModel();
    StudentProgress studentProgress2 =
        StudentProgressFixture.create()
            .withTrainingPath(apcTrainingPath2)
            .withStudent(student)
            .toModel();

    when(studentProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(studentProgressAPC, studentProgress2));

    BddLogger.when("no institutions has enabled navigation field");
    boolean result = institutionService.isNavigationEnabledFor(EPortfolioType.LIFE_PROJECT);

    BddLogger.when("it should return false");
    assertFalse(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentHasNoProgramProgress() {
    BddLogger.given("a InstitutionServiceImpl service");
    when(studentProgressRepository.findAllByStudent(student)).thenReturn(List.of());

    BddLogger.when("student has no program progresses");
    boolean result = institutionService.isNavigationEnabledFor(EPortfolioType.APC);

    BddLogger.when("it should return false");
    assertFalse(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }
}
