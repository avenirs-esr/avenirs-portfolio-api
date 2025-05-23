package fr.avenirsesr.portfolio.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.domain.service.InstitutionServiceImpl;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeInstitution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeProgram;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeProgramProgress;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeUser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class InstitutionServiceImplTest {
  private AutoCloseable closeable;

  @Mock private ProgramProgressRepository programProgressRepository;

  @InjectMocks private InstitutionServiceImpl institutionService;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void shouldReturnTrueWhenInstitutionHasEnabledNavigationField() {
    // Given
    var student = FakeUser.create().withStudent().toModel().toStudent();
    var institutionAPC =
        FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel();
    var programAPC = FakeProgram.of(institutionAPC).isNotAPC().toModel();
    var progressAPC = FakeProgramProgress.of(programAPC, student, Set.of()).toModel();

    var institutionLifeProject =
        FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.LIFE_PROJECT)).toModel();
    var programLifeProject = FakeProgram.of(institutionLifeProject).isNotAPC().toModel();
    var progressLifeProject =
        FakeProgramProgress.of(programLifeProject, student, Set.of()).toModel();

    when(programProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(progressAPC, progressLifeProject));

    // When
    boolean result = institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);

    // Then
    assertTrue(result);
    verify(programProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenNoInstitutionHasEnabledNavigationField() {
    // Given
    var student = FakeUser.create().withStudent().toModel().toStudent();
    var institutionAPC =
        FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel();
    var programAPC = FakeProgram.of(institutionAPC).isNotAPC().toModel();
    var progressAPC = FakeProgramProgress.of(programAPC, student, Set.of()).toModel();

    var institution2 =
        FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel();
    var program2 = FakeProgram.of(institution2).isNotAPC().toModel();
    var progress2 = FakeProgramProgress.of(program2, student, Set.of()).toModel();

    when(programProgressRepository.findAllByStudent(student))
        .thenReturn(List.of(progressAPC, progress2));

    // When
    boolean result =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT);

    // Then
    assertFalse(result);
    verify(programProgressRepository).findAllByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentHasNoProgramProgress() {
    // Given
    var student = FakeUser.create().withStudent().toModel().toStudent();
    when(programProgressRepository.findAllByStudent(student)).thenReturn(List.of());

    // When
    boolean result = institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);

    // Then
    assertFalse(result);
    verify(programProgressRepository).findAllByStudent(student);
  }
}
