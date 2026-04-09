package fr.avenirsesr.portfolio.trace.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.fixture.AMSFixture;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.fixture.DeclaredSkillProgressFixture;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.port.input.TraceAttachmentService;
import fr.avenirsesr.portfolio.file.infrastructure.fixture.TraceAttachmentFixture;
import fr.avenirsesr.portfolio.program.domain.model.Program;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.program.infrastructure.fixture.*;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.fixture.StudentProgressFixture;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationsData;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;
import fr.avenirsesr.portfolio.trace.domain.data.TracesSummaryData;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.client.TraceConfigurationClient;
import fr.avenirsesr.portfolio.trace.infrastructure.fixture.TraceFixture;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TraceServiceImplTest {
  @Mock private TraceRepository traceRepository;
  @Mock private TraceAttachmentService traceAttachmentService;
  @Mock private StudentProgressService studentProgressService;
  @Mock private AssociationService associationService;

  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private TraceConfigurationClient traceConfigurationClient;

  @Mock private UserService userService;
  @Mock private LoggedInUserService loggedInUserService;
  @InjectMocks private TraceServiceImpl traceService;

  private Student student;

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();
  }

  @Nested
  class ConnectedTests {
    @BeforeEach
    void setupGiven() {
      BddLogger.given("a connected student");
      when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());
    }

    @Test
    void givenPageAndPageSize_shouldGetTracesView() {
      BddLogger.given("a TraceServiceImpl service and a pagination configuration");
      int pageNumber = 1;
      int pageSize = 8;
      int totalElement = 13;

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
      DeclaredSkillProgress dsp =
          DeclaredSkillProgressFixture.create().withStudent(student).toModel();
      SkillLevelProgress slp =
          SkillLevelProgressFixture.create(student, SkillLevelFixture.create().toModel()).toModel();

      Trace trace =
          TraceFixture.create()
              .withUser(student.getUser())
              .withAmses(List.of(ams))
              .withDeclaredSkillProgresses(List.of(dsp))
              .withSkillLevels(List.of(slp))
              .toModel();

      BddLogger.when("deleting the trace");
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
      traceService.deleteById(trace.getId());

      BddLogger.then("it should delete the trace and its links to AMS and skill levels");
      verify(traceRepository).save(trace);
      assertTrue(trace.getDeletedAt().isPresent());
      assertTrue(trace.getAmses().isEmpty());
      assertTrue(trace.getSkillLevels().isEmpty());
      assertTrue(trace.getDeclaredSkillProgresses().isEmpty());
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
          assertThrows(TraceNotFoundException.class, () -> traceService.deleteById(trace.getId()));

      BddLogger.then("it should throw TRACE_NOT_FOUND");
      assertEquals(EErrorCode.TRACE_NOT_FOUND, exception.getErrorCode());
      verify(traceRepository, never()).delete(trace);
    }

    @Test
    void givenTraceWithAmsAndSkillLevels_shouldThrowUserNotAuthorizedException() {
      BddLogger.given("a TraceServiceImpl service and a trace with AMS and skill levels");
      User otherUser = UserFixture.create().toModel();
      AMS ams = AMSFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).withAmses(List.of(ams)).toModel();

      BddLogger.when("deleting the trace of another user");
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
      UserNotAuthorizedException exception =
          assertThrows(
              UserNotAuthorizedException.class, () -> traceService.deleteById(trace.getId()));

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
                  .withCreatedAt(Instant.now().minus(86, ChronoUnit.DAYS))
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
      TracesSummaryData summary = traceService.getTracesSummary();

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
      traceService.createTrace(title, language, isGroup, personalNote, iaJustification);

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
      String title = "Trace with null fields";

      BddLogger.when("creating a new trace with null fields");
      traceService.createTrace(title, ELanguage.FRENCH, false, null, null);

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
    void shouldThrowExceptionWhenCreatingTraceWithBlankTitle() {
      BddLogger.given("a TraceServiceImpl service");
      BddLogger.when("creating a new trace with a blank title");
      assertThrows(
          Exception.class,
          () -> traceService.createTrace("   ", ELanguage.FRENCH, false, null, null));
      BddLogger.then("it should throw a validation exception");
      verify(traceRepository, never()).save(any());
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
      when(traceAttachmentService.findByTrace(any()))
          .thenReturn(List.of(TraceAttachmentFixture.create().toModel()));

      BddLogger.when("update trace");
      traceService.updateTrace(
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
      when(traceAttachmentService.findByTrace(any()))
          .thenReturn(List.of(TraceAttachmentFixture.create().toModel()));

      BddLogger.when("update trace with null fields");
      traceService.updateTrace(
          trace.getId(), titleUpdated, languageUpdated, isGroupUpdated, null, null);

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
    void shouldThrowTraceNotFoundWhenUpdatingUnknownTrace() {
      BddLogger.given("a TraceServiceImpl service");
      UUID unknownId = UUID.randomUUID();
      when(traceRepository.findById(unknownId)).thenReturn(Optional.empty());

      BddLogger.when("updating an unknown trace");
      TraceNotFoundException ex =
          assertThrows(
              TraceNotFoundException.class,
              () -> traceService.updateTrace(unknownId, "t", ELanguage.FRENCH, false, null, null));

      BddLogger.then("it should throw TRACE_NOT_FOUND");
      assertEquals(EErrorCode.TRACE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void shouldThrowUserNotAuthorizedWhenUpdatingTraceOfAnotherUser() {
      BddLogger.given("a TraceServiceImpl service and a trace owned by another user");
      User otherUser = UserFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).toModel();
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

      BddLogger.when("updating the trace");
      UserNotAuthorizedException ex =
          assertThrows(
              UserNotAuthorizedException.class,
              () ->
                  traceService.updateTrace(
                      trace.getId(), "x", ELanguage.FRENCH, false, null, null));

      BddLogger.then("it should throw USER_NOT_AUTHORIZED");
      assertEquals(EErrorCode.USER_NOT_AUTHORIZED, ex.getErrorCode());
      verify(traceRepository, never()).save(any());
    }

    @Test
    void shouldThrowFileNotFoundWhenUpdatingTraceAndNoActiveAttachment() {
      BddLogger.given("a TraceServiceImpl service and a trace without active attachment");
      Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
      when(traceRepository.save(any())).thenReturn(trace);
      when(traceAttachmentService.findByTrace(any())).thenReturn(List.of());

      BddLogger.when("updating the trace");
      assertThrows(
          FileNotFoundException.class,
          () -> traceService.updateTrace(trace.getId(), "x", ELanguage.FRENCH, false, null, null));

      BddLogger.then("it should throw FileNotFoundException");
    }

    @Test
    void givenLoggedInUser_shouldReturnLastTracesOverview() {
      BddLogger.given("a TraceServiceImpl service");
      List<Trace> traces =
          List.of(
              TraceFixture.create().withUser(student.getUser()).toModel(),
              TraceFixture.create().withUser(student.getUser()).toModel());

      BddLogger.when("getting last traces overview");
      when(traceRepository.findLastsOf(eq(student.getUser()), anyInt())).thenReturn(traces);
      List<Trace> result = traceService.lastTracesOf();

      BddLogger.then("it should return last traces and call repository with logged user");
      assertEquals(2, result.size());
      verify(traceRepository).findLastsOf(eq(student.getUser()), anyInt());
    }

    @Test
    void givenExistingTrace_shouldReturnTraceDetailData_withAssociations() {
      BddLogger.given(
          "a TraceServiceImpl service and a trace with associations and an active attachment");
      SkillLevelProgress slpWithNoAms = mock(SkillLevelProgress.class);
      SkillLevelProgress slpWithAms = mock(SkillLevelProgress.class);
      DeclaredSkillProgress dsp = mock(DeclaredSkillProgress.class);

      Trace trace =
          TraceFixture.create()
              .withUser(student.getUser())
              .withTitle("Trace title")
              .withLanguage(ELanguage.FRENCH)
              .withGroup(true)
              .withSkillLevels(List.of(slpWithNoAms, slpWithAms))
              .withDeclaredSkillProgresses(List.of(dsp))
              .toModel();

      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
      when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
          .thenReturn(List.of());

      var activeAttachment = TraceAttachmentFixture.create().toModel();
      when(traceAttachmentService.findByTrace(trace)).thenReturn(List.of(activeAttachment));

      BddLogger.when("getting trace detail");
      TraceDetailData detail = traceService.getTraceDetail(trace.getId());

      BddLogger.then("it should return detail with associations and attachment");
      assertEquals(trace.getId(), detail.id());
      assertEquals("Trace title", detail.title());
      assertEquals(EPortfolioType.LIFE_PROJECT.name(), detail.programName());
      assertNotNull(detail.attachment());
    }

    @Test
    void givenTraceWithoutActiveAttachment_shouldThrowFileNotFoundException_onGetTraceDetail() {
      BddLogger.given("a TraceServiceImpl service and a trace without active attachment");
      Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
      when(traceAttachmentService.findByTrace(trace)).thenReturn(List.of());

      BddLogger.when("getting trace detail");
      assertThrows(FileNotFoundException.class, () -> traceService.getTraceDetail(trace.getId()));

      BddLogger.then("it should throw FileNotFoundException");
    }

    @Test
    void givenTraceOfAnotherUser_shouldThrowUserNotAuthorizedException_onGetTraceDetail() {
      BddLogger.given("a TraceServiceImpl service and a trace owned by another user");
      User otherUser = UserFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).toModel();
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

      BddLogger.when("getting trace detail");
      UserNotAuthorizedException ex =
          assertThrows(
              UserNotAuthorizedException.class, () -> traceService.getTraceDetail(trace.getId()));

      BddLogger.then("it should throw USER_NOT_AUTHORIZED");
      assertEquals(EErrorCode.USER_NOT_AUTHORIZED, ex.getErrorCode());
    }

    @Test
    void givenAssociatedDeclaredSkillProgress_shouldReturnTraces() {
      BddLogger.given("two traces associated to the given declaredSkillProgress");
      DeclaredSkillProgress declaredSkillProgress =
          DeclaredSkillProgressFixture.create().withStudent(student).toModel();
      Trace trace1 =
          TraceFixture.create()
              .withUser(student.getUser())
              .withDeclaredSkillProgresses(List.of(declaredSkillProgress))
              .toModel();
      Trace trace2 =
          TraceFixture.create()
              .withUser(student.getUser())
              .withDeclaredSkillProgresses(List.of(declaredSkillProgress))
              .toModel();
      when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());
      when(traceRepository.linkedWith(declaredSkillProgress)).thenReturn(List.of(trace1, trace2));

      BddLogger.when("getting two traces associated to DeclaredSkillProgress");
      List<Trace> traces =
          traceService.getTracesLinkedWithDeclaredSkillProgress(declaredSkillProgress);

      BddLogger.then(
          "it should return two traces associated to DeclaredSkillProgress and right user");
      assertEquals(2, traces.size());
      assertEquals(trace1, traces.getFirst());
      assertEquals(trace2, traces.getLast());
    }

    @Test
    void givenAssociatedDeclaredSkillProgress_shouldThrowUserNotAuthorizedException() {
      BddLogger.given("two traces associated to the given declaredSkillProgress");
      Student anotherStudent = StudentFixture.create().toModel();
      DeclaredSkillProgress declaredSkillProgress =
          DeclaredSkillProgressFixture.create().withStudent(student).toModel();
      Trace trace1 =
          TraceFixture.create()
              .withUser(student.getUser())
              .withDeclaredSkillProgresses(List.of(declaredSkillProgress))
              .toModel();
      Trace trace2 =
          TraceFixture.create()
              .withUser(student.getUser())
              .withDeclaredSkillProgresses(List.of(declaredSkillProgress))
              .toModel();
      when(loggedInUserService.getLoggedInUser()).thenReturn(anotherStudent.getUser());
      when(traceRepository.linkedWith(declaredSkillProgress)).thenReturn(List.of(trace1, trace2));

      BddLogger.when(
          "getting two traces associated to DeclaredSkillProgress but to another student");
      assertThrows(
          UserNotAuthorizedException.class,
          () -> traceService.getTracesLinkedWithDeclaredSkillProgress(declaredSkillProgress));
    }

    @Test
    void givenTraceWithDeclaredActivityAssociation_shouldReturnTraceAssociations() {
      BddLogger.given("a trace with declared activity associations");

      UUID traceId = UUID.randomUUID();

      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

      UUID activityId = UUID.randomUUID();

      Association association = mock(Association.class);
      when(association.getAssociationType()).thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
      when(association.getId1()).thenReturn(activityId);

      DeclaredActivity activity = mock(DeclaredActivity.class);
      when(activity.getId()).thenReturn(activityId);

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      when(associationService.getAllOf(
              traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
          .thenReturn(List.of(association));

      when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(activityId)))
          .thenReturn(List.of(activity));

      BddLogger.when("getting trace associations");

      TraceAssociationsData result = traceService.getTraceAssociations(traceId, false);

      BddLogger.then("it should return declared activity associations");

      assertEquals(1, result.declaredActivityAssociations().size());
    }

    @Test
    void givenUnknownTrace_shouldThrowTraceNotFoundException_onGetTraceAssociations() {
      BddLogger.given("unknown trace id");

      UUID traceId = UUID.randomUUID();

      when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

      BddLogger.when("getting trace associations");

      assertThrows(
          TraceNotFoundException.class, () -> traceService.getTraceAssociations(traceId, false));

      BddLogger.then("it should throw TraceNotFoundException");
    }

    @Test
    void givenTraceOfAnotherUser_shouldThrowUserNotAuthorized_onGetTraceAssociations() {
      BddLogger.given("a trace owned by another user");

      UUID traceId = UUID.randomUUID();

      User otherUser = UserFixture.create().toModel();

      Trace trace = TraceFixture.create().withUser(otherUser).withId(traceId).toModel();

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      BddLogger.when("getting trace associations");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> traceService.getTraceAssociations(traceId, false));

      BddLogger.then("it should throw UserNotAuthorizedException");
    }

    @Test
    void givenTraceWithDeclaredActivity_whenOnlyNotCompleted_shouldCallFindAllNotCompleted() {
      BddLogger.given("a trace with a declared activity association");
      UUID traceId = UUID.randomUUID();
      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();
      UUID activityId = UUID.randomUUID();

      Association association = mock(Association.class);
      when(association.getAssociationType()).thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
      when(association.getId1()).thenReturn(activityId);

      DeclaredActivity activity = mock(DeclaredActivity.class);
      when(activity.getId()).thenReturn(activityId);

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
      when(associationService.getAllOf(
              traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
          .thenReturn(List.of(association));

      when(declaredActivityService.findAllNotCompletedActivitiesByIds(eq(List.of(activityId))))
          .thenReturn(List.of(activity));

      BddLogger.when("getting ONLY NOT COMPLETED trace associations");
      TraceAssociationsData result = traceService.getTraceAssociations(traceId, true);

      BddLogger.then("it should call the specific repository method and return the data");
      verify(declaredActivityService).findAllNotCompletedActivitiesByIds(eq(List.of(activityId)));
      verify(declaredActivityService, never()).findAllDeclaredActivitiesByIds(any());
      assertEquals(1, result.declaredActivityAssociations().size());
    }

    @Test
    void givenTraceAndDeclaredSkills_shouldAssociateTraceWithDeclaredSkills() {
      BddLogger.given("a trace and declared skills");

      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();

      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

      DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);
      when(skill.getId()).thenReturn(skillId);
      when(skill.getStudent()).thenReturn(student);

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      when(associationService.getAllOf(
              traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
          .thenReturn(List.of());

      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(any()))
          .thenReturn(List.of(skill));

      BddLogger.when("associating trace with declared skills");

      TraceAssociationsData result =
          traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId));

      BddLogger.then("association should be created");

      verify(associationService).createAll(any());

      assertNotNull(result);
    }

    @Test
    void givenUnknownTrace_shouldThrowTraceNotFound_whenAssociateTraceWithDeclaredSkill() {
      BddLogger.given("unknown trace");

      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();

      when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

      BddLogger.when("associating");

      assertThrows(
          TraceNotFoundException.class,
          () -> traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId)));

      BddLogger.then("it should throw TraceNotFoundException");
    }

    @Test
    void
        givenTraceOfAnotherUser_shouldThrowUserNotAuthorized_whenAssociateTraceWithDeclaredSkill() {
      BddLogger.given("trace of another user");

      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();

      User otherUser = UserFixture.create().toModel();

      Trace trace = TraceFixture.create().withUser(otherUser).withId(traceId).toModel();

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      BddLogger.when("associating");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId)));

      BddLogger.then("should throw UserNotAuthorizedException");
    }

    @Test
    void givenMissingSkill_shouldThrowDeclaredSkillProgressNotFound() {
      BddLogger.given("missing skill");

      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();

      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
          .thenReturn(List.of());

      BddLogger.when("associating");

      assertThrows(
          fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception
              .DeclaredSkillProgressNotFoundException.class,
          () -> traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId)));

      BddLogger.then("should throw DeclaredSkillProgressNotFoundException");
    }

    @Test
    void givenSkillOfAnotherUser_shouldThrowUserNotAuthorized() {
      BddLogger.given("skill of another user");

      UUID traceId = UUID.randomUUID();
      UUID skillId = UUID.randomUUID();

      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

      Student otherStudent = StudentFixture.create().toModel();

      DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);
      when(skill.getId()).thenReturn(skillId);
      when(skill.getStudent()).thenReturn(otherStudent);

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
          .thenReturn(List.of(skill));

      BddLogger.when("associating");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId)));

      BddLogger.then("should throw UserNotAuthorizedException");
    }

    @Test
    void givenValidTraceAndAssociations_shouldUnassociateSuccessfully() {
      BddLogger.given("a TraceServiceImpl service, a valid trace and valid association IDs");
      UUID traceId = UUID.randomUUID();
      UUID associationId1 = UUID.randomUUID();
      UUID associationId2 = UUID.randomUUID();
      List<UUID> idsToDelete = List.of(associationId1, associationId2);

      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();
      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      Association assoc1 = mock(Association.class);
      when(assoc1.getId()).thenReturn(associationId1);
      Association assoc2 = mock(Association.class);
      when(assoc2.getId()).thenReturn(associationId2);

      when(associationService.getAllOf(
              traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
          .thenReturn(List.of(assoc1, assoc2));

      BddLogger.when("unassociating traces");
      traceService.unassociate(traceId, idsToDelete);

      BddLogger.then("it should call the association service to delete the associations");
      verify(associationService).deleteAllByIds(idsToDelete);
    }

    @Test
    void givenUnknownTrace_shouldThrowTraceNotFound_whenUnassociating() {
      BddLogger.given("an unknown trace id");
      UUID traceId = UUID.randomUUID();
      List<UUID> idsToDelete = List.of(UUID.randomUUID());

      when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

      BddLogger.when("unassociating");
      assertThrows(
          TraceNotFoundException.class, () -> traceService.unassociate(traceId, idsToDelete));

      BddLogger.then("it should throw TraceNotFoundException and never delete");
      verify(associationService, never()).deleteAllByIds(any());
    }

    @Test
    void givenTraceOfAnotherUser_shouldThrowUserNotAuthorized_whenUnassociating() {
      BddLogger.given("a trace owned by another user");
      UUID traceId = UUID.randomUUID();
      List<UUID> idsToDelete = List.of(UUID.randomUUID());

      User otherUser = UserFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).withId(traceId).toModel();

      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      BddLogger.when("unassociating");
      assertThrows(
          UserNotAuthorizedException.class, () -> traceService.unassociate(traceId, idsToDelete));

      BddLogger.then("it should throw UserNotAuthorizedException and never delete");
      verify(associationService, never()).deleteAllByIds(any());
    }

    @Test
    void unassociate_should_delete_associations_when_they_belong_to_trace() {
      BddLogger.given("A logged-in user, an existing trace, and valid association IDs");
      UUID traceId = UUID.randomUUID();
      UUID associationId1 = UUID.randomUUID();
      UUID associationId2 = UUID.randomUUID();
      List<UUID> idsToDelete = List.of(associationId1, associationId2);

      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();
      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      Association assoc1 = mock(Association.class);
      when(assoc1.getId()).thenReturn(associationId1);
      Association assoc2 = mock(Association.class);
      when(assoc2.getId()).thenReturn(associationId2);

      when(associationService.getAllOf(
              traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
          .thenReturn(List.of(assoc1, assoc2));

      BddLogger.when("unassociate is called");
      traceService.unassociate(traceId, idsToDelete);

      BddLogger.then("associationService.deleteAllByIds should be called");
      verify(associationService).deleteAllByIds(idsToDelete);
    }

    @Test
    void unassociate_should_throw_TraceNotFoundException_when_trace_not_found() {
      BddLogger.given("An unknown trace id");
      UUID traceId = UUID.randomUUID();
      List<UUID> idsToDelete = List.of(UUID.randomUUID());

      when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

      BddLogger.when("unassociate is called");
      BddLogger.then("TraceNotFoundException is thrown");

      assertThrows(
          TraceNotFoundException.class, () -> traceService.unassociate(traceId, idsToDelete));

      verify(associationService, never()).deleteAllByIds(any());
    }

    @Test
    void unassociate_should_throw_UserNotAuthorizedException_when_trace_belongs_to_other_user() {
      BddLogger.given("A trace owned by another user");
      List<UUID> idsToDelete = List.of(UUID.randomUUID());

      User otherUser = UserFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).toModel();

      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

      BddLogger.when("unassociate is called");
      BddLogger.then("UserNotAuthorizedException is thrown");

      assertThrows(
          UserNotAuthorizedException.class,
          () -> traceService.unassociate(trace.getId(), idsToDelete));

      verify(associationService, never()).deleteAllByIds(any());
    }

    @Test
    void unassociate_should_throw_AssociationDoesNotExistException_when_ids_not_linked_to_trace() {
      BddLogger.given("A valid trace but association IDs not linked to it");
      UUID linkedAssocId = UUID.randomUUID();
      UUID unlinkedAssocId = UUID.randomUUID();
      List<UUID> idsToDelete = List.of(linkedAssocId, unlinkedAssocId);

      Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
      when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

      Association assoc1 = mock(Association.class);
      when(assoc1.getId()).thenReturn(linkedAssocId);

      when(associationService.getAllOf(
              trace.getId(), Trace.class, EAssociationType.getAllBy(Trace.class)))
          .thenReturn(List.of(assoc1));

      BddLogger.when("unassociate is called with unlinked id");
      BddLogger.then("AssociationDoesNotExistException is thrown");

      assertThrows(
          AssociationDoesNotExistException.class,
          () -> traceService.unassociate(trace.getId(), idsToDelete));

      verify(associationService, never()).deleteAllByIds(any());
    }

    @Test
    void searchDeclaredActivityForAssociation_should_return_mapped_results() {
      BddLogger.given("A valid trace and a list of declared activities");
      UUID traceId = UUID.randomUUID();
      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();
      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      UUID assocActivityId = UUID.randomUUID();
      UUID unassocActivityId = UUID.randomUUID();
      UUID finishedActivityId = UUID.randomUUID();

      Association assoc = mock(Association.class);
      when(assoc.getId1()).thenReturn(assocActivityId);
      when(associationService.getAllOf(
              traceId, Trace.class, List.of(EAssociationType.DECLARED_ACTIVITY_TRACE)))
          .thenReturn(List.of(assoc));

      DeclaredActivity assocActivity = mock(DeclaredActivity.class);
      when(assocActivity.getId()).thenReturn(assocActivityId);
      var act1 = mock(Activity.class);
      when(act1.getTitle()).thenReturn("Activity 1");
      when(assocActivity.getActivity()).thenReturn(act1);

      DeclaredActivity unassocActivity = mock(DeclaredActivity.class);
      when(unassocActivity.getId()).thenReturn(unassocActivityId);
      var act2 = mock(Activity.class);
      when(act2.getTitle()).thenReturn("Activity 2");
      when(unassocActivity.getActivity()).thenReturn(act2);

      DeclaredActivity finishedActivity = mock(DeclaredActivity.class);
      when(finishedActivity.getId()).thenReturn(finishedActivityId);
      var act3 = mock(Activity.class);
      when(act3.getTitle()).thenReturn("Activity 3");
      when(finishedActivity.getActivity()).thenReturn(act3);
      when(finishedActivity.getFinishedAt()).thenReturn(Optional.ofNullable(Instant.now()));

      PageCriteria criteria = new PageCriteria(0, 10);
      PageInfo pageInfo = new PageInfo(0, 10, 2);
      when(declaredActivityService.searchDeclaredActivity("kw", criteria))
          .thenReturn(
              new PagedResult<>(
                  List.of(assocActivity, unassocActivity, finishedActivity), pageInfo));

      BddLogger.when("searching declared activities for association");
      var result = traceService.searchDeclaredActivityForAssociation(traceId, "kw", criteria);

      BddLogger.then("it should return mapped results with correct disabled flags");
      assertEquals(3, result.content().size());
      assertTrue(result.content().get(0).disabled());
      assertFalse(result.content().get(1).disabled());
      assertTrue(result.content().get(2).disabled());
    }

    @Test
    void searchDeclaredSkillForAssociation_should_return_mapped_results() {
      BddLogger.given("A valid trace and a list of declared skills");
      UUID traceId = UUID.randomUUID();
      Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();
      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      UUID assocSkillId = UUID.randomUUID();
      UUID unassocSkillId = UUID.randomUUID();

      Association assoc = mock(Association.class);
      when(assoc.getId2()).thenReturn(assocSkillId);
      when(associationService.getAllOf(
              traceId, Trace.class, List.of(EAssociationType.TRACE_DECLARED_SKILL)))
          .thenReturn(List.of(assoc));

      DeclaredSkillProgress assocSkillProg = mock(DeclaredSkillProgress.class);
      when(assocSkillProg.getId()).thenReturn(assocSkillId);
      var skill1 = mock(fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill.class);
      when(skill1.getLibelle()).thenReturn("Skill 1");
      when(assocSkillProg.getSkill()).thenReturn(skill1);

      DeclaredSkillProgress unassocSkillProg = mock(DeclaredSkillProgress.class);
      when(unassocSkillProg.getId()).thenReturn(unassocSkillId);
      var skill2 = mock(fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill.class);
      when(skill2.getLibelle()).thenReturn("Skill 2");
      when(unassocSkillProg.getSkill()).thenReturn(skill2);

      PageCriteria criteria = new PageCriteria(0, 10);
      PageInfo pageInfo = new PageInfo(0, 10, 2);
      when(declaredSkillProgressService.searchDeclaredSkill("kw", criteria))
          .thenReturn(new PagedResult<>(List.of(assocSkillProg, unassocSkillProg), pageInfo));

      BddLogger.when("searching declared skills for association");
      var result = traceService.searchDeclaredSkillForAssociation(traceId, "kw", criteria);

      BddLogger.then("it should return mapped results with correct disabled flags");
      assertEquals(2, result.content().size());
      assertTrue(result.content().get(0).disabled());
      assertFalse(result.content().get(1).disabled());
    }

    @Test
    void searchDeclaredActivityForAssociation_should_throw_TraceNotFoundException() {
      BddLogger.given("an unknown trace id");
      UUID traceId = UUID.randomUUID();
      when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

      BddLogger.when("searching declared activities");
      assertThrows(
          TraceNotFoundException.class,
          () ->
              traceService.searchDeclaredActivityForAssociation(
                  traceId, "", new PageCriteria(0, 10)));

      BddLogger.then("it should throw TraceNotFoundException");
    }

    @Test
    void searchDeclaredSkillForAssociation_should_throw_TraceNotFoundException() {
      BddLogger.given("an unknown trace id");
      UUID traceId = UUID.randomUUID();
      when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

      BddLogger.when("searching declared skills");
      assertThrows(
          TraceNotFoundException.class,
          () ->
              traceService.searchDeclaredSkillForAssociation(traceId, "", new PageCriteria(0, 10)));

      BddLogger.then("it should throw TraceNotFoundException");
    }

    @Test
    void searchDeclaredActivityForAssociation_should_throw_UserNotAuthorizedException() {
      BddLogger.given("a trace owned by another user");
      UUID traceId = UUID.randomUUID();
      User otherUser = UserFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).withId(traceId).toModel();
      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      BddLogger.when("searching declared activities");
      assertThrows(
          UserNotAuthorizedException.class,
          () ->
              traceService.searchDeclaredActivityForAssociation(
                  traceId, "", new PageCriteria(0, 10)));

      BddLogger.then("it should throw UserNotAuthorizedException");
    }

    @Test
    void searchDeclaredSkillForAssociation_should_throw_UserNotAuthorizedException() {
      BddLogger.given("a trace owned by another user");
      UUID traceId = UUID.randomUUID();
      User otherUser = UserFixture.create().toModel();
      Trace trace = TraceFixture.create().withUser(otherUser).withId(traceId).toModel();
      when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

      BddLogger.when("searching declared skills");
      assertThrows(
          UserNotAuthorizedException.class,
          () ->
              traceService.searchDeclaredSkillForAssociation(traceId, "", new PageCriteria(0, 10)));

      BddLogger.then("it should throw UserNotAuthorizedException");
    }
  }

  @Nested
  class NotConnectedTests {
    @BeforeEach
    void setupGiven() {
      BddLogger.given("a non connected student");
    }

    @Test
    void givenTraceWithoutSkillLevels_shouldReturnLifeProject() {
      BddLogger.given("a TraceServiceImpl service and a trace without skill levels");
      Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
      when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
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

      when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
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

      when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
          .thenReturn(List.of(studentProgress));

      BddLogger.when("getting the progam name of the trace");
      String result = traceService.programNameOfTrace(trace);

      BddLogger.then("it should return the APC program name");
      assertEquals("Program name", result);
    }

    @Test
    void givenUnassociatedTrace_shouldReturnWillBeDeletedAt() {
      BddLogger.given("a TraceServiceImpl service and an unassociated trace");
      TraceConfiguration config = new TraceConfiguration(90, 30, 5);
      when(traceConfigurationClient.getTraceConfiguration()).thenReturn(config);

      Instant createdAt = Instant.now().minus(10, ChronoUnit.DAYS);
      Trace trace =
          TraceFixture.create().withUser(student.getUser()).withCreatedAt(createdAt).toModel();

      trace.setSkillLevels(List.of());
      trace.setAmses(List.of());
      trace.setDeclaredSkillProgresses(List.of());
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

      SkillLevelProgress skillLevelProgress =
          SkillLevelProgressFixture.create(student, SkillLevelFixture.create().toModel()).toModel();
      trace.setSkillLevels(List.of(skillLevelProgress));

      assertFalse(trace.isUnassociated());

      BddLogger.when("getting willBeDeletedAt");
      Optional<LocalDate> willBeDeletedAt = traceService.getWillBeDeletedAt(trace);

      BddLogger.then("it should return empty");
      assertTrue(willBeDeletedAt.isEmpty());
    }
  }

  @Test
  void givenTraceWithoutSkillLevels_shouldReturnLifeProject() {
    BddLogger.given("a TraceServiceImpl service and a trace without skill levels");
    Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
    when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
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

    when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
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

    when(studentProgressService.findStudentProgressesBySkillLevelProgresses(any()))
        .thenReturn(List.of(studentProgress));

    BddLogger.when("getting the progam name of the trace");
    String result = traceService.programNameOfTrace(trace);

    BddLogger.then("it should return the APC program name");
    assertEquals("Program name", result);
  }
}
