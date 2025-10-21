package fr.avenirsesr.portfolio.trace.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture.AdditionalSkillProgressFixture;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.TraceAttachmentRepository;
import fr.avenirsesr.portfolio.file.infrastructure.fixture.TraceAttachmentFixture;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.SkillLevelProgressRepository;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.trace.domain.data.TracesSummaryData;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.service.StudentServiceImpl;
import fr.avenirsesr.portfolio.user.domain.service.TeacherServiceImpl;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
  @Mock private StudentRepository studentRepository;
  @Mock private TraceAttachmentRepository traceAttachmentRepository;
  @Mock private StudentProgressRepository studentProgressRepository;
  @Mock private AMSRepository amsRepository;
  @Mock private SkillLevelProgressRepository skillLevelProgressRepository;
  @Mock private AdditionalSkillProgressRepository additionalSkillProgressRepository;

  @Mock private TraceConfigurationClient traceConfigurationClient;

  @Mock private StudentServiceImpl studentService;
  @Mock private TeacherServiceImpl teacherService;
  @InjectMocks private TraceServiceImpl traceService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
  }

  @Test
  void givenTraceWithoutSkillLevels_shouldReturnLifeProject() {
    BddLogger.given("a TraceServiceImpl service and a trace without skill levels");
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(any()))
        .thenReturn(List.of());

    BddLogger.when("getting the progam name of the trace");
    String result = traceService.programNameOfTrace(trace);

    BddLogger.then("it should return LIFE_PROJECT");
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTraceWithSkillLevelsButNoApc_shouldReturnLifeProject() {
    BddLogger.given("a TraceServiceImpl service and a trace with skill levels and without APC");
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
            .withStudent(student)
            .toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelProgress))
            .toModel();

    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(any()))
        .thenReturn(List.of(studentProgress));

    BddLogger.when("getting the progam name of the trace");
    String result = traceService.programNameOfTrace(trace);

    BddLogger.then("it should return LIFE_PROJECT");
    assertEquals("LIFE_PROJECT", result);
  }

  @Test
  void givenTraceWithApcProgram_shouldReturnProgramName() {
    BddLogger.given("a TraceServiceImpl service and a trace with APC program");
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
            .withStudent(student)
            .toModel();
    Trace trace =
        TraceFixture.create()
            .withUser(student.getUser())
            .withSkillLevels(List.of(skillLevelProgress))
            .toModel();

    when(studentProgressRepository.findStudentProgressesBySkillLevelProgresses(any()))
        .thenReturn(List.of(studentProgress));

    BddLogger.when("getting the progam name of the trace");
    String result = traceService.programNameOfTrace(trace);

    BddLogger.then("it should return the APC program name");
    assertEquals("Program name", result);
  }

  @Test
  void givenPageAndPageSize_shouldGetTracesView() {
    BddLogger.given("a TraceServiceImpl service and a pagination configuration");
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

    BddLogger.when("getting the traces view");
    when(traceRepository.findAll(
            student.getUser(),
            null,
            new TraceFilter(false, null, null, null),
            null,
            new PageCriteria(pageNumber, pageSize)))
        .thenReturn(new PagedResult<>(traces, new PageInfo(pageNumber, pageSize, totalElement)));
    PagedResult<Trace> traceView =
        traceService.getTracesView(
            student.getUser(),
            null,
            new TraceFilter(false, null, null, null),
            null,
            new PageCriteria(pageNumber, pageSize));

    BddLogger.then("it should return the traces view");
    assertEquals(traces.size(), traceView.content().size());
    assertEquals(pageSize, traceView.pageInfo().pageSize());
    assertEquals(totalElement, traceView.pageInfo().totalElements());
    assertEquals(pageNumber, traceView.pageInfo().page());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldDeleteTraceAndLinksToAmsAndSkillLevels() {
    BddLogger.given("a TraceServiceImpl service and a trace with AMS and skill levels");
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();

    BddLogger.when("deleting the trace");
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    traceService.deleteById(student.getUser(), trace.getId());

    BddLogger.then("it should delete the trace and its links to AMS and skill levels");
    verify(traceRepository).save(trace);
    assertTrue(trace.getDeletedAt().isPresent());
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldThrowTraceNotFoundException() {
    BddLogger.given("a TraceServiceImpl service and a trace with AMS and skill levels");
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();

    BddLogger.when("deleting the trace but the trace is not found");
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.empty());
    TraceNotFoundException exception =
        assertThrows(
            TraceNotFoundException.class,
            () -> traceService.deleteById(student.getUser(), trace.getId()));

    BddLogger.then("it should throw TRACE_NOT_FOUND");
    assertEquals(EErrorCode.TRACE_NOT_FOUND, exception.getErrorCode());
    verify(traceRepository, never()).delete(trace);
  }

  @Test
  void givenTraceWithAmsAndSkillLevels_shouldThrowUserNotAuthorizedException() {
    BddLogger.given("a TraceServiceImpl service and a trace with AMS and skill levels");
    User otherUser = UserFixture.create().toModel();
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();

    BddLogger.when("deleting the trace of another user");
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    UserNotAuthorizedException exception =
        assertThrows(
            UserNotAuthorizedException.class,
            () -> traceService.deleteById(otherUser, trace.getId()));

    BddLogger.then("it should throw UserNotAuthorizedException");
    assertEquals(EErrorCode.USER_NOT_AUTHORIZED, exception.getErrorCode());
    verify(traceRepository, never()).delete(trace);
  }

  @Test
  void givenTraces_shouldReturnSummary() {
    BddLogger.given("a TraceServiceImpl service and unassociated traces");
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

    List<Trace> associatedTraces =
        List.of(
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(12, ChronoUnit.DAYS))
                .toModel(),
            TraceFixture.create()
                .withUser(student.getUser())
                .withCreatedAt(Instant.now().minus(72, ChronoUnit.DAYS))
                .toModel());

    BddLogger.when("getting the traces summary");
    when(traceRepository.findAll(student.getUser(), false)).thenReturn(unassociatedTraces);
    when(traceRepository.findAll(student.getUser(), true)).thenReturn(associatedTraces);
    when(traceConfigurationClient.getTraceConfiguration()).thenReturn(traceConfiguration);
    TracesSummaryData summary = traceService.getTracesSummary(student.getUser());

    BddLogger.then("it should return the traces summary");
    assertEquals(4, summary.unassociated());
    assertEquals(2, summary.associated());
    assertEquals(3, summary.totalWarnings());
    assertEquals(1, summary.totalCriticals());
  }

  @Test
  void shouldCreateAndSaveNewTrace() {
    BddLogger.given("a TraceServiceImpl service");
    User user = student.getUser();
    String title = "Test Title";
    ELanguage language = ELanguage.FRENCH;
    boolean isGroup = true;
    String personalNote = "Some personal note";
    String iaJustification = "Justified by AI";

    BddLogger.when("creating a new trace");
    traceService.createTrace(user, title, language, isGroup, personalNote, iaJustification);

    BddLogger.then("it should create and save the new trace");
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
    BddLogger.given("a TraceServiceImpl service");
    User user = student.getUser();
    String title = "Trace with null fields";

    BddLogger.when("creating a new trace with null fields");
    traceService.createTrace(user, title, ELanguage.FRENCH, false, null, null);

    BddLogger.then("it should create and save the new trace with null fields");
    ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
    verify(traceRepository).save(captor.capture());

    Trace trace = captor.getValue();

    assertEquals(title, trace.getTitle());
    assertEquals(ELanguage.FRENCH, trace.getLanguage());
    assertTrue(trace.getPersonalNote().isEmpty());
    assertTrue(trace.getAiUseJustification().isEmpty());
  }

  @Test
  void shouldUpdateAndSaveTrace() {
    BddLogger.given("a TraceServiceImpl service and a valid trace");
    User user = student.getUser();
    String title = "Test Title";
    String titleUpdated = "Test Title - Updated";
    ELanguage language = ELanguage.ENGLISH;
    ELanguage languageUpdated = ELanguage.FRENCH;
    boolean isGroup = false;
    boolean isGroupUpdated = true;
    String personalNote = "Some personal note";
    String personalNoteUpdated = "Some personal note - Updated";
    String aiJustification = "Justified by AI";
    String aiJustificationUpdated = "Justified by AI - Updated";

    Trace trace =
        TraceFixture.create()
            .withUser(user)
            .withTitle(title)
            .withLanguage(language)
            .withGroup(isGroup)
            .withPersonalNote(personalNote)
            .withAiUseJustification(aiJustification)
            .toModel();
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    when(traceRepository.save(trace)).thenReturn(trace);
    when(traceAttachmentRepository.findByTrace(any()))
        .thenReturn(List.of(TraceAttachmentFixture.create().toModel()));

    BddLogger.when("update trace");
    traceService.updateTrace(
        user,
        trace.getId(),
        titleUpdated,
        languageUpdated,
        isGroupUpdated,
        personalNoteUpdated,
        aiJustificationUpdated);

    BddLogger.then("it should update and save the trace");
    ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
    verify(traceRepository).save(captor.capture());

    Trace captorTrace = captor.getValue();

    assertEquals(user, captorTrace.getUser());
    assertEquals(titleUpdated, captorTrace.getTitle());
    assertEquals(languageUpdated, captorTrace.getLanguage());
    assertEquals(isGroupUpdated, captorTrace.isGroup());

    assertTrue(captorTrace.getPersonalNote().isPresent());
    assertEquals(personalNoteUpdated, captorTrace.getPersonalNote().get());

    assertTrue(captorTrace.getAiUseJustification().isPresent());
    assertEquals(aiJustificationUpdated, captorTrace.getAiUseJustification().get());
  }

  @Test
  void shouldUpdateTraceWithNullFields() {
    BddLogger.given("a TraceServiceImpl service and a valid trace");
    User user = student.getUser();
    String title = "Test Title";
    String titleUpdated = "Test Title with null fields";
    ELanguage language = ELanguage.ENGLISH;
    ELanguage languageUpdated = ELanguage.FRENCH;
    boolean isGroup = false;
    boolean isGroupUpdated = true;
    String personalNote = "Some personal note";
    String aiJustification = "Justified by AI";

    Trace trace =
        TraceFixture.create()
            .withUser(user)
            .withTitle(title)
            .withLanguage(language)
            .withGroup(isGroup)
            .withPersonalNote(personalNote)
            .withAiUseJustification(aiJustification)
            .toModel();
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
    when(traceRepository.save(trace)).thenReturn(trace);
    when(traceAttachmentRepository.findByTrace(any()))
        .thenReturn(List.of(TraceAttachmentFixture.create().toModel()));

    BddLogger.when("update trace with null fields");
    traceService.updateTrace(
        user, trace.getId(), titleUpdated, languageUpdated, isGroupUpdated, null, null);

    BddLogger.then("it should update and save the trace with null fields");
    ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
    verify(traceRepository).save(captor.capture());

    Trace captorTrace = captor.getValue();

    assertEquals(user, captorTrace.getUser());
    assertEquals(titleUpdated, captorTrace.getTitle());
    assertEquals(languageUpdated, captorTrace.getLanguage());
    assertEquals(isGroupUpdated, captorTrace.isGroup());
    assertFalse(captorTrace.getPersonalNote().isPresent());
    assertFalse(captorTrace.getAiUseJustification().isPresent());
  }

  @Test
  void givenUnassociatedTrace_shouldReturnWillBeDeletedAt() {
    BddLogger.given("a TraceServiceImpl service and an unassociated trace");
    TraceConfiguration config = new TraceConfiguration(90, 30, 5);
    when(traceConfigurationClient.getTraceConfiguration()).thenReturn(config);

    Instant createdAt = Instant.now().minus(10, ChronoUnit.DAYS);
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withCreatedAt(createdAt).toModel();

    // Mock isUnassociated() à true
    trace.setSkillLevels(List.of()); // pour simuler une trace non associée
    assertTrue(trace.isUnassociated());

    BddLogger.when("getting willBeDeletedAt");
    Optional<LocalDate> willBeDeletedAt = traceService.getWillBeDeletedAt(trace);

    BddLogger.then("it should return willBeDeletedAt");
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
    BddLogger.given("a TraceServiceImpl service and an associated trace");
    TraceConfiguration config = new TraceConfiguration(90, 30, 5);
    when(traceConfigurationClient.getTraceConfiguration()).thenReturn(config);

    Instant createdAt = Instant.now();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withCreatedAt(createdAt).toModel();

    // Simule une trace associée → isUnassociated() == false
    SkillLevelProgress skillLevelProgress =
        SkillLevelProgressFixture.create(student, SkillLevelFixture.create().toModel()).toModel();
    trace.setSkillLevels(List.of(skillLevelProgress));

    assertFalse(trace.isUnassociated());

    BddLogger.when("getting willBeDeletedAt");
    Optional<LocalDate> willBeDeletedAt = traceService.getWillBeDeletedAt(trace);

    BddLogger.then("it should return empty");
    assertTrue(willBeDeletedAt.isEmpty());
  }

  @Test
  void givenValidTraceAndIds_shouldAssociateAllAndSave() {
    BddLogger.given(
        "a trace belonging to the student and valid AMS, skillLevels and additional skills");
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

    AMS ams = AMSFixture.create().toModel();
    SkillLevelProgress skillLevel =
        SkillLevelProgressFixture.create(student, SkillLevelFixture.create().toModel()).toModel();
    AdditionalSkillProgress additional = AdditionalSkillProgressFixture.create().toModel();

    when(amsRepository.findAllByStudent(any(Student.class))).thenReturn(List.of(ams));
    when(skillLevelProgressRepository.findAllByStudent(any(Student.class)))
        .thenReturn(List.of(skillLevel));
    when(additionalSkillProgressRepository.findAllByStudent(any(Student.class)))
        .thenReturn(List.of(additional));
    when(studentService.getStudentById(any(UUID.class))).thenReturn(student);

    BddLogger.when("associating AMS, SkillLevels and AdditionalSkills");
    traceService.associateTrace(
        student.getUser(),
        trace.getId(),
        List.of(ams.getId()),
        List.of(skillLevel.getId()),
        List.of(additional.getId()));

    BddLogger.then("trace should be associated and saved");
    verify(traceRepository).save(trace);
    assertTrue(trace.getAmses().contains(ams));
    assertTrue(trace.getSkillLevels().contains(skillLevel));
    assertTrue(trace.getAdditionalSkillProgresses().contains(additional));
  }

  @Test
  void givenNonExistentTrace_shouldThrowTraceNotFoundException() {
    BddLogger.given("a non-existent trace");
    UUID traceId = UUID.randomUUID();
    when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

    BddLogger.when("associating");
    TraceNotFoundException ex =
        assertThrows(
            TraceNotFoundException.class,
            () ->
                traceService.associateTrace(
                    student.getUser(), traceId, List.of(), List.of(), List.of()));

    BddLogger.then("TRACE_NOT_FOUND should be thrown");
    assertEquals(EErrorCode.TRACE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void givenTraceOfAnotherUser_shouldThrowUserNotAuthorizedException() {
    BddLogger.given("a trace belonging to another user");
    User otherUser = UserFixture.create().toModel();
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

    BddLogger.when("associating with another user");
    UserNotAuthorizedException ex =
        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                traceService.associateTrace(
                    otherUser, trace.getId(), List.of(), List.of(), List.of()));

    BddLogger.then("USER_NOT_AUTHORIZED should be thrown");
    assertEquals(EErrorCode.USER_NOT_AUTHORIZED, ex.getErrorCode());
  }

  @Test
  void givenAlreadyAssociatedAMS_shouldThrowUserNotAuthorizedException() {
    BddLogger.given("a trace already containing an AMS");
    AMS ams = AMSFixture.create().toModel();
    Trace trace =
        TraceFixture.create().withUser(student.getUser()).withAmses(List.of(ams)).toModel();
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

    BddLogger.when("associating the same AMS again");
    assertThrows(
        UserNotAuthorizedException.class,
        () ->
            traceService.associateTrace(
                student.getUser(), trace.getId(), List.of(ams.getId()), List.of(), List.of()));
  }

  @Test
  void givenSkillLevelNotOwnedByStudent_shouldThrowUserNotAuthorizedException() {
    BddLogger.given("a trace and a skillLevel not owned by the student");
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
    UUID fakeSkillLevelId = UUID.randomUUID();
    when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

    BddLogger.when("associating a skillLevel not owned by the student");
    assertThrows(
        UserNotAuthorizedException.class,
        () ->
            traceService.associateTrace(
                student.getUser(), trace.getId(), List.of(), List.of(fakeSkillLevelId), List.of()));
  }
}
