package fr.avenirsesr.portfolio.trace.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.port.input.TraceConfigurationService;
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
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.UnassociatedTracesSummary;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

  @Mock private TraceConfigurationService traceConfigurationService;

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
    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(any()))
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

    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(any()))
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

    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(any()))
        .thenReturn(List.of(studentProgress));

    // When
    String result = traceService.programNameOfTrace(trace);

    // Then
    assertEquals("Program name", result);
  }

  @Test
  void givenPageAndPageSize_shouldGetTracesView() {
    // Given
    int pageNumber = 1;
    int pageSize = 8;
    int totalElement = 13;
    TraceConfiguration traceConfiguration = new TraceConfiguration(90, 30, 5);

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
    when(traceRepository.findAll(
            student.getUser(), new PageCriteria(pageNumber, pageSize), ETraceStatus.UNASSOCIATED))
        .thenReturn(new PagedResult<>(traces, new PageInfo(pageNumber, pageSize, totalElement)));
    PagedResult<Trace> traceView =
        traceService.getTracesView(
            student.getUser(), new PageCriteria(pageNumber, pageSize), ETraceStatus.UNASSOCIATED);

    // Then
    assertEquals(traces.size(), traceView.content().size());
    assertEquals(pageSize, traceView.pageInfo().pageSize());
    assertEquals(totalElement, traceView.pageInfo().totalElements());
    assertEquals(pageNumber, traceView.pageInfo().page());
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
    verify(traceRepository).save(trace);
    assertTrue(trace.getDeletedAt().isPresent());
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
    verify(traceRepository, never()).delete(trace);
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
    verify(traceRepository, never()).delete(trace);
  }

  @Test
  void givenUnassociatedTraces_shouldReturnSummary() {
    // Given
    TraceConfiguration traceConfiguration = new TraceConfiguration(90, 30, 5);
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
    when(traceConfigurationService.getTraceConfiguration()).thenReturn(traceConfiguration);
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

  @Test
  void givenUnassociatedTrace_shouldReturnWillBeDeletedAt() {
    // Given
    TraceConfiguration config = new TraceConfiguration(90, 30, 5);
    when(traceConfigurationService.getTraceConfiguration()).thenReturn(config);

    Instant createdAt = Instant.now().minus(10, ChronoUnit.DAYS);
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withCreatedAt(createdAt).toModel();

    // Mock isUnassociated() à true
    trace.setSkillLevels(List.of()); // pour simuler une trace non associée
    assertTrue(trace.isUnassociated());

    // When
    Optional<LocalDate> willBeDeletedAt = traceService.getWillBeDeletedAt(trace);

    // Then
    assertTrue(willBeDeletedAt.isPresent());
    LocalDate expectedDate =
        createdAt
            .plus(Duration.ofDays(config.maxRemainingDays()))
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    assertEquals(expectedDate, willBeDeletedAt.get());
  }

  @Test
  void givenAssociatedTrace_shouldReturnEmpty() {
    // Given
    TraceConfiguration config = new TraceConfiguration(90, 30, 5);
    when(traceConfigurationService.getTraceConfiguration()).thenReturn(config);

    Instant createdAt = Instant.now();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withCreatedAt(createdAt).toModel();

    // Simule une trace associée → isUnassociated() == false
    SkillLevelProgress skillLevelProgress =
        SkillLevelProgressFixture.create(student, SkillLevelFixture.create().toModel()).toModel();
    trace.setSkillLevels(List.of(skillLevelProgress));

    assertFalse(trace.isUnassociated());

    // When
    Optional<LocalDate> willBeDeletedAt = traceService.getWillBeDeletedAt(trace);

    // Then
    assertTrue(willBeDeletedAt.isEmpty());
  }
}
