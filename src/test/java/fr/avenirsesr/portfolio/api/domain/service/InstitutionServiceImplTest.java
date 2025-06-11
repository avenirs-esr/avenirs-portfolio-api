package fr.avenirsesr.portfolio.api.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fixtures.ProgramProgressFixture;
import fixtures.UserFixture;
import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class InstitutionServiceImplTest {
  private AutoCloseable closeable;
  private Student student;
  private ELanguage language = ELanguage.FRENCH;

  @Mock private ProgramProgressRepository programProgressRepository;

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
    ProgramProgress progressAPC =
        ProgramProgressFixture.createWithAPC().withStudent(student).toModel();
    ProgramProgress progressLifeProject =
        ProgramProgressFixture.createWithoutAPC().withStudent(student).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progressAPC, progressLifeProject));

    // When
    boolean result =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.APC, language);

    // Then
    assertTrue(result);
    verify(programProgressRepository).findAllByStudent(student, language);
  }

  @Test
  void shouldReturnFalseWhenNoInstitutionHasEnabledNavigationField() {
    // Given
    ProgramProgress progressAPC =
        ProgramProgressFixture.createWithAPC().withStudent(student).toModel();
    ProgramProgress progress2 =
        ProgramProgressFixture.createWithAPC().withStudent(student).toModel();

    when(programProgressRepository.findAllByStudent(student, language))
        .thenReturn(List.of(progressAPC, progress2));

    // When
    boolean result =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT, language);

    // Then
    assertFalse(result);
    verify(programProgressRepository).findAllByStudent(student, language);
  }

  @Test
  void shouldReturnFalseWhenStudentHasNoProgramProgress() {
    // Given
    when(programProgressRepository.findAllByStudent(student, language)).thenReturn(List.of());

    // When
    boolean result =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.APC, language);

    // Then
    assertFalse(result);
    verify(programProgressRepository).findAllByStudent(student, language);
  }
}
