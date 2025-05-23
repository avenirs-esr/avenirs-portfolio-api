package fr.avenirsesr.portfolio.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.domain.service.ProgramProgressServiceImpl;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeInstitution;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeProgram;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeProgramProgress;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeUser;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProgramProgressServiceImplTest {
  private AutoCloseable closeable;

  @Mock private ProgramProgressRepository programProgressRepository;

  @InjectMocks private ProgramProgressServiceImpl programProgressService;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  void shouldReturnTrueWhenStudentIsFollowingProgramWithLearningMethod() {
    // Given
    var student = FakeUser.create().withStudent().toModel().toStudent();
    var institutionAPC =
        FakeInstitution.create().withEnabledFiled(Set.of(EPortfolioType.APC)).toModel();
    var programAPC = FakeProgram.of(institutionAPC).isNotAPC().toModel();
    var progressAPC = FakeProgramProgress.of(programAPC, student, Set.of()).toModel();

    when(programProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of(progressAPC));

    // When
    boolean result = programProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertTrue(result);
    verify(programProgressRepository).findAllAPCByStudent(student);
  }

  @Test
  void shouldReturnFalseWhenStudentIsNotFollowingAnyProgramWithLearningMethod() {
    // Given
    var student = FakeUser.create().withStudent().toModel().toStudent();
    when(programProgressRepository.findAllAPCByStudent(student)).thenReturn(List.of());

    // When
    boolean result = programProgressService.isStudentFollowingAPCProgram(student);

    // Then
    assertFalse(result);
    verify(programProgressRepository).findAllAPCByStudent(student);
  }
}
