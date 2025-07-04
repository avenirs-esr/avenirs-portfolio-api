package fr.avenirsesr.portfolio.student.progress.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.ProgramProgressFixture;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
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
  private ELanguage language = ELanguage.FRENCH;

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
    TrainingPath progressAPC =
        ProgramProgressFixture.createWithAPC().withStudent(student).toModel();
    TrainingPath progressLifeProject =
        ProgramProgressFixture.createWithoutAPC().withStudent(student).toModel();

    when(studentProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(progressAPC, progressLifeProject));

    // When
    boolean result = institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);

    // Then
    assertTrue(result);
    verify(studentProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenNoInstitutionHasEnabledNavigationField() {
    // Given
    TrainingPath progressAPC =
        ProgramProgressFixture.createWithAPC().withStudent(student).toModel();
    TrainingPath progress2 =
        ProgramProgressFixture.createWithAPC().withStudent(student).toModel();

    when(studentProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(progressAPC, progress2));

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
