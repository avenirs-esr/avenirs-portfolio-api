package fr.avenirsesr.portfolio.program.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.program.domain.model.Institution;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.output.client.InstitutionConfigClient;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.InstitutionFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.ProgramFixture;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.TrainingPathFixture;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceImplTest {

  @Mock private StudentProgressService studentProgressService;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private InstitutionConfigClient institutionConfigClient;

  @InjectMocks private InstitutionServiceImpl institutionService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
  }

  @Test
  void shouldAggregateConfigurationAcrossInstitutions_anyTrue() {
    BddLogger.given("a student with progresses in multiple institutions");

    UUID institutionId1 = UUID.randomUUID();
    UUID institutionId2 = UUID.randomUUID();

    Institution inst1 = InstitutionFixture.create().withId(institutionId1).toModel();
    Institution inst2 = InstitutionFixture.create().withId(institutionId2).toModel();
    Program prog1 = ProgramFixture.create().withInstitution(inst1).toModel();
    Program prog2 = ProgramFixture.create().withInstitution(inst2).toModel();
    TrainingPath tp1 = TrainingPathFixture.createWithAPC().withProgram(prog1).toModel();
    TrainingPath tp2 = TrainingPathFixture.createWithoutAPC().withProgram(prog2).toModel();

    StudentProgress sp1 =
        StudentProgressFixture.create().withTrainingPath(tp1).withStudent(student).toModel();
    StudentProgress sp2 =
        StudentProgressFixture.create().withTrainingPath(tp2).withStudent(student).toModel();

    when(studentProgressService.findAllStudentProgressesByStudent(student))
        .thenReturn(List.of(sp1, sp2));

    when(institutionConfigClient.getInstitutionConfigElementsById(institutionId1))
        .thenReturn(new InstitutionConfigurationElements(true, false));
    when(institutionConfigClient.getInstitutionConfigElementsById(institutionId2))
        .thenReturn(new InstitutionConfigurationElements(false, true));

    BddLogger.when("getting the aggregated institution configuration");
    InstitutionConfigurationElements result = institutionService.getInstitutionConfiguration();

    BddLogger.then("it should OR the flags across configs");
    assertTrue(result.apcEnabled());
    assertTrue(result.lifeProjectEnabled());

    verify(studentProgressService).findAllStudentProgressesByStudent(student);
    verify(institutionConfigClient).getInstitutionConfigElementsById(institutionId1);
    verify(institutionConfigClient).getInstitutionConfigElementsById(institutionId2);
    verifyNoMoreInteractions(institutionConfigClient);
  }

  @Test
  void shouldCallConfigClientOnlyOncePerInstitution_distinct() {
    BddLogger.given("a student with multiple progresses in the same institution");

    UUID institutionId = UUID.randomUUID();

    Institution inst1 = InstitutionFixture.create().withId(institutionId).toModel();
    Institution inst2 = InstitutionFixture.create().withId(institutionId).toModel();
    Program prog1 = ProgramFixture.create().withInstitution(inst1).toModel();
    Program prog2 = ProgramFixture.create().withInstitution(inst2).toModel();
    TrainingPath tp1 = TrainingPathFixture.createWithAPC().withProgram(prog1).toModel();
    TrainingPath tp2 = TrainingPathFixture.createWithAPC().withProgram(prog2).toModel();

    StudentProgress sp1 =
        StudentProgressFixture.create().withTrainingPath(tp1).withStudent(student).toModel();
    StudentProgress sp2 =
        StudentProgressFixture.create().withTrainingPath(tp2).withStudent(student).toModel();

    when(studentProgressService.findAllStudentProgressesByStudent(student))
        .thenReturn(List.of(sp1, sp2));

    when(institutionConfigClient.getInstitutionConfigElementsById(institutionId))
        .thenReturn(new InstitutionConfigurationElements(true, true));

    BddLogger.when("getting the aggregated institution configuration");
    InstitutionConfigurationElements result = institutionService.getInstitutionConfiguration();

    BddLogger.then("it should call the client once thanks to distinct()");
    assertTrue(result.apcEnabled());
    assertTrue(result.lifeProjectEnabled());

    verify(studentProgressService).findAllStudentProgressesByStudent(student);
    verify(institutionConfigClient, times(1)).getInstitutionConfigElementsById(institutionId);
    verifyNoMoreInteractions(institutionConfigClient);
  }

  @Test
  void shouldReturnAllFalseWhenStudentHasNoProgress() {
    BddLogger.given("a student with no progresses");
    when(studentProgressService.findAllStudentProgressesByStudent(student)).thenReturn(List.of());

    BddLogger.when("getting the aggregated institution configuration");
    InstitutionConfigurationElements result = institutionService.getInstitutionConfiguration();

    BddLogger.then("it should return a configuration with all disabled");
    assertFalse(result.apcEnabled());
    assertFalse(result.lifeProjectEnabled());

    verify(studentProgressService).findAllStudentProgressesByStudent(student);
    verifyNoInteractions(institutionConfigClient);
  }

  @Test
  void shouldReturnFalseForMissingFlagsWhenAllConfigsAreFalse() {
    BddLogger.given("institutions configs where all flags are disabled");

    UUID institutionId1 = UUID.randomUUID();
    UUID institutionId2 = UUID.randomUUID();

    Institution inst1 = InstitutionFixture.create().withId(institutionId1).toModel();
    Institution inst2 = InstitutionFixture.create().withId(institutionId2).toModel();
    Program prog1 = ProgramFixture.create().withInstitution(inst1).toModel();
    Program prog2 = ProgramFixture.create().withInstitution(inst2).toModel();
    TrainingPath tp1 = TrainingPathFixture.createWithAPC().withProgram(prog1).toModel();
    TrainingPath tp2 = TrainingPathFixture.createWithAPC().withProgram(prog2).toModel();

    StudentProgress sp1 =
        StudentProgressFixture.create().withTrainingPath(tp1).withStudent(student).toModel();
    StudentProgress sp2 =
        StudentProgressFixture.create().withTrainingPath(tp2).withStudent(student).toModel();

    when(studentProgressService.findAllStudentProgressesByStudent(student))
        .thenReturn(List.of(sp1, sp2));

    when(institutionConfigClient.getInstitutionConfigElementsById(institutionId1))
        .thenReturn(new InstitutionConfigurationElements(false, false));
    when(institutionConfigClient.getInstitutionConfigElementsById(institutionId2))
        .thenReturn(new InstitutionConfigurationElements(false, false));

    BddLogger.when("getting the aggregated institution configuration");
    InstitutionConfigurationElements result = institutionService.getInstitutionConfiguration();

    BddLogger.then("it should return all false");
    assertFalse(result.apcEnabled());
    assertFalse(result.lifeProjectEnabled());

    verify(institutionConfigClient).getInstitutionConfigElementsById(institutionId1);
    verify(institutionConfigClient).getInstitutionConfigElementsById(institutionId2);
    verifyNoMoreInteractions(institutionConfigClient);
  }
}
