package fr.avenirsesr.portfolio.program.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class InstitutionServiceImplTest {
  private AutoCloseable closeable;
  private Student student;

  @Mock private StudentProgressRepository studentProgressRepository;

  @InjectMocks private InstitutionServiceImpl institutionService;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void shouldReturnTrueWhenInstitutionHasEnabledNavigationField() {
    // Given
    TrainingPath apcTrainingPath = TrainingPathFixture.createWithAPC().toModel();
    TrainingPath lifeProjectTrainingPath = TrainingPathFixture.createWithoutAPC().toModel();
    StudentProgress studentProgressAPC =
        StudentProgressFixture.create()
            .withTrainingPath(apcTrainingPath)
            .withUser(student.getUser())
            .toModel();
    StudentProgress studentProgressLifeProject =
        StudentProgressFixture.create()
            .withTrainingPath(lifeProjectTrainingPath)
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(studentProgressAPC, studentProgressLifeProject));

    // When
    boolean result = institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);

    // Then
    assertTrue(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenNoInstitutionHasEnabledNavigationField() {
    // Given
    TrainingPath apcTrainingPath = TrainingPathFixture.createWithAPC().toModel();
    TrainingPath apcTrainingPath2 = TrainingPathFixture.createWithAPC().toModel();
    StudentProgress studentProgressAPC =
        StudentProgressFixture.create()
            .withTrainingPath(apcTrainingPath)
            .withUser(student.getUser())
            .toModel();
    StudentProgress studentProgress2 =
        StudentProgressFixture.create()
            .withTrainingPath(apcTrainingPath2)
            .withUser(student.getUser())
            .toModel();

    when(studentProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(studentProgressAPC, studentProgress2));

    // When
    boolean result =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT);

    // Then
    assertFalse(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentHasNoProgramProgress() {
    // Given
    when(studentProgressRepository.findAllByStudent(student)).thenReturn(List.of());

    // When
    boolean result = institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);

    // Then
    assertFalse(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }
}
