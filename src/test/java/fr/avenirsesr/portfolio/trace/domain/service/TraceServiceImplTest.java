package fr.avenirsesr.portfolio.trace.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.configuration.domain.model.TraceConfigurationInfo;
import fr.avenirsesr.portfolio.configuration.domain.port.input.ConfigurationService;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.TraceView;
import fr.avenirsesr.portfolio.trace.domain.model.UnassociatedTracesSummary;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TraceServiceImplTest {
  @Mock private TraceRepository traceRepository;
  @Mock private StudentProgressRepository studentProgressRepository;

  @Mock private ConfigurationService configurationService;

  @InjectMocks private TraceServiceImpl traceService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = UserFixture.createStudent().toModel().toStudent();
  }

  @Test
  void givenTraceWithoutSkillLevels_shouldReturnLifeProject() {
    // Given
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(
            any(Student.class), any()))
        .thenReturn(List.of());

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTraceWithSkillLevelsButNoApc_shouldReturnLifeProject() {
    // Given
    Program program = ProgramFixture.create().withAPC(false).toModel();
    TrainingPath progress = TrainingPathFixture.create().withProgram(program).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().toModel();
    SkillLevelProgress skillLevelProgress =
        SkillLevelProgressFixture.create(student, skillLevel)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withTrainingPath(progress)
            .withSkillLevels(List.of(skillLevelProgress))
            .withUser(student.getUser())
            .toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelProgress))
            .toModel();

    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(
            any(Student.class), any()))
        .thenReturn(List.of(studentProgress));

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTraceWithApcProgram_shouldReturnProgramName() {
    // Given
    Program program = ProgramFixture.create().withAPC(true).withName("Program name").toModel();
    TrainingPath progress = TrainingPathFixture.create().withProgram(program).toModel();
    SkillLevel skillLevel = SkillLevelFixture.create().toModel();
    SkillLevelProgress skillLevelProgress =
        SkillLevelProgressFixture.create(student, skillLevel)
            .withStatus(ESkillLevelStatus.TO_BE_EVALUATED)
            .toModel();
    StudentProgress studentProgress =
        StudentProgressFixture.create()
            .withTrainingPath(progress)
            .withSkillLevels(List.of(skillLevelProgress))
            .withUser(student.getUser())
            .toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelProgress))
            .toModel();

    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(
            any(Student.class), any()))
        .thenReturn(List.of(studentProgress));

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals("Program name", result);
  }

  @Test
  void givenPageAndPageSize_shouldGetUnassociatedTraces() {
    // Given
    int pageNumber = 1;
    int pageSize = 8;
    int totalElement = 13;
    TraceConfigurationInfo traceConfigurationInfo = new TraceConfigurationInfo(90, 30, 5);

    List<Trace> traces =
        List.of(
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(83, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(84, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(85, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(86, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(87, ChronoUnit.DAYS))
                .toModel());

    // When
    when(traceRepository.findAllUnassociated(
            student.getUser(), new PageCriteria(pageNumber, pageSize)))
        .thenReturn(new PagedResult<>(traces, new PageInfo(pageNumber, pageSize, totalElement)));
    when(configurationService.getTraceConfiguration()).thenReturn(traceConfigurationInfo);
    TraceView traceView =
        traceService.getUnassociatedTraces(
            student.getUser(), new PageCriteria(pageNumber, pageSize));

    // Then
    assertEquals(traces.size(), traceView.traces().size());
    assertEquals(3, traceView.criticalCount());
    assertEquals(pageSize, traceView.page().pageSize());
    assertEquals(totalElement, traceView.page().totalElements());
    assertEquals(pageNumber, traceView.page().page());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldDeleteTraceAndLinksToAmsAndSkillLevels() {
    // Given
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();

    // When
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    traceService.deleteById(student.getUser(), trace.getId());

    // Then
    verify(traceRepository).deleteById(trace.getId());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldThrowTraceNotFoundException() {
    // Given
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();

    // When
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.empty());
    TraceNotFoundException exception =
        assertThrows(
            TraceNotFoundException.class,
            () -> traceService.deleteById(student.getUser(), trace.getId()));

    // Then
    assertEquals(EErrorCode.TRACE_NOT_FOUND, exception.getErrorCode());
    verify(traceRepository, never()).deleteById(trace.getId());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldThrowUserNotAuthorizedException() {
    // Given
    User otherUser = UserFixture.createStudent().toModel();
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();

    // When
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    UserNotAuthorizedException exception =
        assertThrows(
            UserNotAuthorizedException.class,
            () -> traceService.deleteById(otherUser, trace.getId()));

    // Then
    assertEquals(EErrorCode.USER_NOT_AUTHORIZED, exception.getErrorCode());
    verify(traceRepository, never()).deleteById(trace.getId());
  }

  @Test
  void givenUnassociatedTraces_shouldReturnSummary() {
    // Given
    TraceConfigurationInfo traceConfigurationInfo = new TraceConfigurationInfo(90, 30, 5);
    List<Trace> unassociatedTraces =
        List.of(
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(12, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(72, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(84, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(85, ChronoUnit.DAYS))
                .toModel());

    // When
    when(traceRepository.findAllUnassociated(student.getUser())).thenReturn(unassociatedTraces);
    when(configurationService.getTraceConfiguration()).thenReturn(traceConfigurationInfo);
    UnassociatedTracesSummary summary =
        traceService.getUnassociatedTracesSummary(student.getUser());

    // Then
    assertEquals(4, summary.total());
    assertEquals(3, summary.totalWarnings());
    assertEquals(1, summary.totalCriticals());
  }

  @Test
  void shouldCreateAndSaveNewTrace() {
    // Given
    User user = student.getUser();
    String title = "Test Title";
    ELanguage language = ELanguage.FRENCH;
    boolean isGroup = true;
    String personalNote = "Some personal note";
    String iaJustification = "Justified by AI";

    // When
    traceService.createTrace(user, title, language, isGroup, personalNote, iaJustification);

    // Then
    ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
    verify(traceRepository).save(captor.capture());

    Trace trace = captor.getValue();

    assertEquals(user, trace.getUser());

    assertNotNull(trace.getId());
    assertEquals(title, trace.getTitle());
    assertEquals(language, trace.getLanguage());
    assertEquals(isGroup, trace.isGroup());

    assertTrue(trace.getPersonalNote().isPresent());
    assertEquals(personalNote, trace.getPersonalNote().get());

    assertTrue(trace.getAiUseJustification().isPresent());
    assertEquals(iaJustification, trace.getAiUseJustification().get());
  }

  @Test
  void shouldCreateTraceWithNullFields() {
    // Given
    User user = student.getUser();
    String title = "Trace with null fields";

    // When
    traceService.createTrace(user, title, ELanguage.FRENCH, false, null, null);

    // Then
    ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
    verify(traceRepository).save(captor.capture());

    Trace trace = captor.getValue();

    assertEquals(title, trace.getTitle());
    assertEquals(ELanguage.FRENCH, trace.getLanguage());
    assertTrue(trace.getPersonalNote().isEmpty());
    assertTrue(trace.getAiUseJustification().isEmpty());
  }
}
