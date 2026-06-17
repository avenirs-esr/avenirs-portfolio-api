package fr.avenirsesr.portfolio.trace.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.association.domain.exception.AssociationDoesNotExistException;
import fr.avenirsesr.portfolio.association.domain.model.Association;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.domain.port.input.AssociationService;
import fr.avenirsesr.portfolio.association.domain.service.AssociationSearchHelper;
import fr.avenirsesr.portfolio.common.configuration.domain.model.TraceConfiguration;
import fr.avenirsesr.portfolio.common.data.domain.model.*;
import fr.avenirsesr.portfolio.common.error.domain.model.enums.EErrorCode;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.exception.FileNotFoundException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.exception.DeclaredActivityAlreadyFinishedException;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.FeedbackService;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.exception.DeclaredExperienceNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.exception.DeclaredSkillProgressNotFoundException;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import fr.avenirsesr.portfolio.trace.domain.data.*;
import fr.avenirsesr.portfolio.trace.domain.exception.TraceNotFoundException;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
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
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceServiceImplTest {

  @Mock private TraceRepository traceRepository;
  @Mock private UserService userService;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private DeclaredSkillProgressService declaredSkillProgressService;
  @Mock private DeclaredExperienceService declaredExperienceService;
  @Mock private TraceConfigurationClient traceConfigurationClient;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private AssociationService associationService;
  @Mock private AssociationSearchHelper associationSearchHelper;
  @Mock private FeedbackService feedbackService;
  @Mock private FileRepository fileRepository;

  @InjectMocks private TraceServiceImpl traceService;

  private Student student;

  private static final TraceConfiguration DEFAULT_CONFIG = new TraceConfiguration(90, 30, 5);

  @BeforeEach
  void setUp() {
    student = StudentFixture.create().toModel();

    lenient()
        .when(
            associationService.getAllOf(
                ArgumentMatchers.<List<UUID>>any(),
                eq(Trace.class),
                eq(List.of(EAssociationType.DECLARED_ACTIVITY_TRACE))))
        .thenReturn(List.of());

    lenient()
        .when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of()))
        .thenReturn(List.of());

    lenient()
        .when(declaredActivityService.getDeclaredActivityStatus(List.of()))
        .thenReturn(Map.of());
  }

  @Nested
  class GivenConnectedStudent {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("a connected student");
      lenient().when(loggedInUserService.getLoggedInUser()).thenReturn(student.getUser());
    }

    @Nested
    class WhenGettingTracesView {

      @Test
      void thenItShouldReturnTheTracesView() {
        BddLogger.when("getting the traces view");

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
                    .toModel());

        var filter = new TraceFilter(false, null, null, null);
        var pageCriteria = new PageCriteria(pageNumber, pageSize);

        when(traceRepository.findAll(student.getUser(), null, filter, null, pageCriteria))
            .thenReturn(
                new PagedResult<>(traces, new PageInfo(pageNumber, pageSize, totalElement)));
        when(traceConfigurationClient.getTraceConfiguration()).thenReturn(DEFAULT_CONFIG);
        when(traceRepository.isAssociated(traces))
            .thenReturn(traces.stream().collect(Collectors.toMap(t -> t, t -> false)));

        PagedResult<TraceViewData> result =
            traceService.getTracesView(null, filter, null, pageCriteria);

        BddLogger.then("it should return the traces view");

        assertEquals(traces.size(), result.content().size());
        assertEquals(pageNumber, result.pageInfo().page());
        assertEquals(pageSize, result.pageInfo().pageSize());
        assertEquals(totalElement, result.pageInfo().totalElements());
      }

      @Test
      void thenItShouldReturnWillBeDeletedAtWhenTraceIsUnassociated() {
        BddLogger.when("getting traces view with an unassociated trace");

        Instant createdAt = Instant.now().minus(10, ChronoUnit.DAYS);
        Trace trace =
            TraceFixture.create().withUser(student.getUser()).withCreatedAt(createdAt).toModel();

        var filter = new TraceFilter(false, null, null, null);
        var pageCriteria = new PageCriteria(0, 8);

        when(traceConfigurationClient.getTraceConfiguration()).thenReturn(DEFAULT_CONFIG);
        when(traceRepository.findAll(student.getUser(), null, filter, null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(trace), new PageInfo(0, 8, 1)));
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, false));

        PagedResult<TraceViewData> result =
            traceService.getTracesView(null, filter, null, pageCriteria);

        BddLogger.then("it should return willBeDeletedAt");

        LocalDate expectedDate =
            createdAt
                .plus(Duration.ofDays(DEFAULT_CONFIG.maxRemainingDays()))
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        var returnedTrace = result.content().getFirst();

        assertFalse(returnedTrace.isAssociated());
        assertEquals(expectedDate, returnedTrace.willBeDeletedAt().get());
      }

      @Test
      void thenItShouldReturnEmptyWillBeDeletedAtWhenTraceIsAssociated() {
        BddLogger.when("getting traces view with an associated trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

        var filter = new TraceFilter(false, null, null, null);
        var pageCriteria = new PageCriteria(0, 8);

        when(traceConfigurationClient.getTraceConfiguration()).thenReturn(DEFAULT_CONFIG);
        when(traceRepository.findAll(student.getUser(), null, filter, null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(trace), new PageInfo(0, 8, 1)));
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, true));

        PagedResult<TraceViewData> result =
            traceService.getTracesView(null, filter, null, pageCriteria);

        BddLogger.then("it should return empty willBeDeletedAt");

        var returnedTrace = result.content().getFirst();

        assertTrue(returnedTrace.isAssociated());
      }
    }

    @Nested
    class WhenGettingTracesSummary {

      @Test
      void thenItShouldReturnSummary() {
        BddLogger.when("getting the traces summary");

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
                TraceFixture.create().withUser(student.getUser()).toModel(),
                TraceFixture.create().withUser(student.getUser()).toModel());

        when(traceRepository.findAll(student.getUser(), false)).thenReturn(unassociatedTraces);
        when(traceRepository.findAll(student.getUser(), true)).thenReturn(associatedTraces);
        when(traceConfigurationClient.getTraceConfiguration()).thenReturn(DEFAULT_CONFIG);

        TracesSummaryData result = traceService.getTracesSummary();

        BddLogger.then("it should return the traces summary");

        assertEquals(4, result.unassociated());
        assertEquals(2, result.associated());
        assertEquals(3, result.totalWarnings());
        assertEquals(1, result.totalCriticals());
      }
    }

    @Nested
    class WhenCreatingTrace {

      @Test
      void thenItShouldCreateAndSaveNewTrace() {
        BddLogger.when("creating a new trace");

        String title = "Test Title";
        ELanguage language = ELanguage.FRENCH;
        ETraceAuthorType authorType = ETraceAuthorType.PERSONAL;
        String personalNote = "Some personal note";
        String aiJustification = "Justified by AI";
        String link = "https://example.com";

        traceService.createTrace(title, language, authorType, personalNote, aiJustification, link);

        BddLogger.then("it should create and save the new trace");

        ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
        verify(traceRepository).save(captor.capture());

        Trace trace = captor.getValue();

        assertEquals(student.getUser(), trace.getUser());
        assertNotNull(trace.getId());
        assertEquals(title, trace.getTitle());
        assertEquals(language, trace.getLanguage());
        assertEquals(authorType, trace.getAuthorType());
        assertTrue(trace.getPersonalNote().isPresent());
        assertEquals(personalNote, trace.getPersonalNote().get());
        assertTrue(trace.getLink().isPresent());
        assertEquals(link, trace.getLink().get());
        assertTrue(trace.getAiUseJustification().isPresent());
        assertEquals(aiJustification, trace.getAiUseJustification().get());
      }

      @Test
      void thenItShouldCreateTraceWithNullFields() {
        BddLogger.when("creating a new trace with null fields");

        traceService.createTrace(
            "Trace with null fields",
            ELanguage.FRENCH,
            ETraceAuthorType.PERSONAL,
            null,
            null,
            null);

        BddLogger.then("it should create and save the new trace with null fields");

        ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
        verify(traceRepository).save(captor.capture());

        Trace trace = captor.getValue();

        assertEquals("Trace with null fields", trace.getTitle());
        assertEquals(ELanguage.FRENCH, trace.getLanguage());
        assertEquals(ETraceAuthorType.PERSONAL, trace.getAuthorType());
        assertTrue(trace.getPersonalNote().isEmpty());
        assertTrue(trace.getLink().isEmpty());
        assertTrue(trace.getAiUseJustification().isEmpty());
      }

      @Test
      void thenItShouldThrowWhenTitleIsBlank() {
        BddLogger.when("creating a new trace with a blank title");

        assertThrows(
            Exception.class,
            () ->
                traceService.createTrace(
                    "   ", ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null));

        BddLogger.then("it should throw a validation exception");

        verify(traceRepository, never()).save(any());
      }
    }

    @Nested
    class WhenUpdatingTrace {

      @Test
      void thenItShouldUpdateAndSaveTrace() {
        BddLogger.when("updating a trace");

        Trace trace =
            TraceFixture.create()
                .withUser(student.getUser())
                .withTitle("Test Title")
                .withLanguage(ELanguage.ENGLISH)
                .withAuthorType(ETraceAuthorType.PERSONAL)
                .withPersonalNote("Some personal note")
                .withAiUseJustification("Justified by AI")
                .toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.save(trace)).thenReturn(trace);
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, false));

        traceService.updateTrace(
            trace.getId(),
            "Test Title - Updated",
            ELanguage.FRENCH,
            ETraceAuthorType.COLLECTIVE,
            "Some personal note - Updated",
            "Justified by AI - Updated");

        BddLogger.then("it should update and save the trace");

        ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
        verify(traceRepository).save(captor.capture());

        Trace updatedTrace = captor.getValue();

        assertEquals(student.getUser(), updatedTrace.getUser());
        assertEquals("Test Title - Updated", updatedTrace.getTitle());
        assertEquals(ELanguage.FRENCH, updatedTrace.getLanguage());
        assertEquals(ETraceAuthorType.COLLECTIVE, updatedTrace.getAuthorType());
        assertTrue(updatedTrace.getPersonalNote().isPresent());
        assertTrue(updatedTrace.getAiUseJustification().isPresent());
        assertEquals("Some personal note - Updated", updatedTrace.getPersonalNote().get());
        assertEquals("Justified by AI - Updated", updatedTrace.getAiUseJustification().get());
      }

      @Test
      void thenItShouldUpdateTraceWithLink() {
        BddLogger.when("updating a trace with link");

        Trace trace =
            TraceFixture.create()
                .withUser(student.getUser())
                .withLink("https://example.com")
                .toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.save(trace)).thenReturn(trace);
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, false));

        traceService.updateTrace(
            trace.getId(),
            "Updated title",
            ELanguage.FRENCH,
            ETraceAuthorType.THIRD_PARTY,
            "Updated note",
            "Updated justification",
            "https://example.com/updated");

        BddLogger.then("it should update and save the link");

        ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
        verify(traceRepository).save(captor.capture());

        assertTrue(captor.getValue().getLink().isPresent());
        assertEquals("https://example.com/updated", captor.getValue().getLink().get());
        assertEquals(ETraceAuthorType.THIRD_PARTY, captor.getValue().getAuthorType());
      }

      @Test
      void thenItShouldUpdateTraceWithNullFields() {
        BddLogger.when("updating a trace with null optional fields");

        Trace trace =
            TraceFixture.create()
                .withUser(student.getUser())
                .withPersonalNote("Some personal note")
                .withAiUseJustification("Justified by AI")
                .toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.save(trace)).thenReturn(trace);
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, false));

        traceService.updateTrace(
            trace.getId(),
            "Updated title",
            ELanguage.FRENCH,
            ETraceAuthorType.PERSONAL,
            null,
            null);

        BddLogger.then("it should update and clear nullable fields");

        ArgumentCaptor<Trace> captor = ArgumentCaptor.forClass(Trace.class);
        verify(traceRepository).save(captor.capture());

        Trace updatedTrace = captor.getValue();

        assertEquals("Updated title", updatedTrace.getTitle());
        assertEquals(ELanguage.FRENCH, updatedTrace.getLanguage());
        assertEquals(ETraceAuthorType.PERSONAL, updatedTrace.getAuthorType());
        assertTrue(updatedTrace.getPersonalNote().isEmpty());
        assertTrue(updatedTrace.getAiUseJustification().isEmpty());
      }

      @Test
      void thenItShouldReturnDeletableFalseWhenAssociatedTraceIsLocked() {
        BddLogger.when("updating an associated locked trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.save(trace)).thenReturn(trace);
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, true));

        TraceDetailData result =
            traceService.updateTrace(
                trace.getId(), "Updated", ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null);

        BddLogger.then("it should return a non deletable trace detail");

        assertTrue(result.isAssociated());
      }

      @Test
      void thenItShouldReturnDeletableTrueWhenAssociatedTraceIsUnlocked() {
        BddLogger.when("updating an associated unlocked trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.save(trace)).thenReturn(trace);
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, true));

        TraceDetailData result =
            traceService.updateTrace(
                trace.getId(), "Updated", ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null);

        BddLogger.then("it should return a deletable trace detail");

        assertTrue(result.isAssociated());
      }

      @Test
      void thenItShouldThrowTraceNotFoundWhenTraceDoesNotExist() {
        BddLogger.when("updating an unknown trace");

        UUID unknownId = UUID.randomUUID();
        when(traceRepository.findById(unknownId)).thenReturn(Optional.empty());

        TraceNotFoundException exception =
            assertThrows(
                TraceNotFoundException.class,
                () ->
                    traceService.updateTrace(
                        unknownId,
                        "Title",
                        ELanguage.FRENCH,
                        ETraceAuthorType.PERSONAL,
                        null,
                        null,
                        null));

        BddLogger.then("it should throw TRACE_NOT_FOUND");

        assertEquals(EErrorCode.TRACE_NOT_FOUND, exception.getErrorCode());
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenTraceBelongsToAnotherUser() {
        BddLogger.when("updating a trace owned by another user");

        Trace trace = TraceFixture.create().withUser(UserFixture.create().toModel()).toModel();
        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

        UserNotAuthorizedException exception =
            assertThrows(
                UserNotAuthorizedException.class,
                () ->
                    traceService.updateTrace(
                        trace.getId(),
                        "Title",
                        ELanguage.FRENCH,
                        ETraceAuthorType.PERSONAL,
                        null,
                        null,
                        null));

        BddLogger.then("it should throw USER_NOT_AUTHORIZED");

        assertEquals(EErrorCode.USER_NOT_AUTHORIZED, exception.getErrorCode());
        verify(traceRepository, never()).save(any());
      }
    }

    @Nested
    class WhenDeletingTrace {

      @Test
      void thenItShouldDeleteTracesAssociationsAndFiles() {
        BddLogger.when("deleting traces with removable attachments");

        Trace trace = mock(Trace.class);
        File file = mock(File.class);

        UUID traceId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        List<UUID> traceIds = List.of(traceId);

        when(trace.getId()).thenReturn(traceId);
        when(trace.getUser()).thenReturn(student.getUser());
        when(trace.getAttachment()).thenReturn(Optional.of(file));
        when(file.getId()).thenReturn(fileId);

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(trace));
        when(feedbackService.findAttachmentIdsUsedByTraceSnapshots(List.of(), traceIds))
            .thenReturn(Set.of());
        when(fileRepository.findAllById(List.of(fileId))).thenReturn(List.of(file));

        traceService.deleteAllByIds(traceIds);

        BddLogger.then("it should delete files, associations and traces from database");

        verify(fileRepository).removeAllFromDatabase(List.of(file));
        verify(associationService).deleteAllOf(traceIds, Trace.class);
        verify(traceRepository).removeAllFromDatabase(List.of(trace));
      }

      @Test
      void thenItShouldNotDeleteProtectedFiles() {
        BddLogger.when("deleting a trace with an attachment used by a snapshot");

        Trace trace = mock(Trace.class);
        File file = mock(File.class);

        UUID traceId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        List<UUID> traceIds = List.of(traceId);

        when(trace.getId()).thenReturn(traceId);
        when(trace.getUser()).thenReturn(student.getUser());
        when(trace.getAttachment()).thenReturn(Optional.of(file));
        when(file.getId()).thenReturn(fileId);

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(trace));
        when(feedbackService.findAttachmentIdsUsedByTraceSnapshots(List.of(), traceIds))
            .thenReturn(Set.of(fileId));

        traceService.deleteAllByIds(traceIds);

        BddLogger.then("it should keep the protected file but delete associations and traces");

        verify(fileRepository, never()).findAllById(anyList());
        verify(fileRepository, never()).removeAllFromDatabase(anyList());
        verify(associationService).deleteAllOf(traceIds, Trace.class);
        verify(traceRepository).removeAllFromDatabase(List.of(trace));
      }

      @Test
      void thenItShouldThrowFileNotFoundWhenAttachmentDoesNotExist() {
        BddLogger.when("deleting a trace with a missing attachment");

        Trace trace = mock(Trace.class);
        File file = mock(File.class);

        UUID traceId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();
        List<UUID> traceIds = List.of(traceId);

        when(trace.getId()).thenReturn(traceId);
        when(trace.getUser()).thenReturn(student.getUser());
        when(trace.getAttachment()).thenReturn(Optional.of(file));
        when(file.getId()).thenReturn(fileId);

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(trace));
        when(feedbackService.findAttachmentIdsUsedByTraceSnapshots(List.of(), traceIds))
            .thenReturn(Set.of());
        when(fileRepository.findAllById(List.of(fileId))).thenReturn(List.of());

        assertThrows(FileNotFoundException.class, () -> traceService.deleteAllByIds(traceIds));

        BddLogger.then("it should not delete associations and traces");

        verify(associationService, never()).deleteAllOf(anyList(), any());
        verify(traceRepository, never()).removeAllFromDatabase(anyList());
      }

      @Test
      void thenItShouldThrowTraceNotFoundWhenOneTraceDoesNotExist() {
        BddLogger.when("deleting traces with one unknown trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
        UUID missingTraceId = UUID.randomUUID();

        List<UUID> traceIds = List.of(trace.getId(), missingTraceId);

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(trace));

        assertThrows(TraceNotFoundException.class, () -> traceService.deleteAllByIds(traceIds));

        BddLogger.then("it should throw TraceNotFoundException");

        verify(feedbackService, never())
            .findAttachmentIdsUsedByTraceSnapshots(anyList(), anyList());
        verify(fileRepository, never()).findAllById(anyList());
        verify(associationService, never()).deleteAllOf(anyList(), any());
        verify(traceRepository, never()).removeAllFromDatabase(anyList());
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenOneTraceBelongsToAnotherUser() {
        BddLogger.when("deleting traces with one trace owned by another user");

        Trace ownedTrace = TraceFixture.create().withUser(student.getUser()).toModel();
        Trace otherUserTrace =
            TraceFixture.create().withUser(UserFixture.create().toModel()).toModel();

        List<UUID> traceIds = List.of(ownedTrace.getId(), otherUserTrace.getId());

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(ownedTrace, otherUserTrace));

        assertThrows(UserNotAuthorizedException.class, () -> traceService.deleteAllByIds(traceIds));

        BddLogger.then("it should throw UserNotAuthorizedException");

        verify(feedbackService, never())
            .findAttachmentIdsUsedByTraceSnapshots(anyList(), anyList());
        verify(fileRepository, never()).findAllById(anyList());
        verify(associationService, never()).deleteAllOf(anyList(), any());
        verify(traceRepository, never()).removeAllFromDatabase(anyList());
      }
    }

    @Nested
    class WhenGettingTraceDetail {

      @Test
      void thenItShouldReturnAssociatedAndDeletableTraceDetailWhenAssociationsAreUnlocked() {
        BddLogger.when("getting trace detail for an associated unlocked trace");

        Trace trace =
            TraceFixture.create()
                .withUser(student.getUser())
                .withTitle("Trace title")
                .withLanguage(ELanguage.FRENCH)
                .withAuthorType(ETraceAuthorType.PERSONAL)
                .toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, true));

        TraceDetailData result = traceService.getTraceDetail(trace.getId());

        BddLogger.then("it should return a deletable trace detail");

        assertEquals(trace.getId(), result.id());
        assertEquals("Trace title", result.title());
        assertTrue(result.isAssociated());
        assertEquals(EPortfolioType.LIFE_PROJECT.name(), result.programName());
        assertEquals(ETraceAuthorType.PERSONAL, result.authorType());
      }

      @Test
      void thenItShouldReturnAssociatedAndNotDeletableTraceDetailWhenAssociationsAreLocked() {
        BddLogger.when("getting trace detail for an associated locked trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, true));

        TraceDetailData result = traceService.getTraceDetail(trace.getId());

        BddLogger.then("it should return a non deletable trace detail");

        assertTrue(result.isAssociated());
      }

      @Test
      void thenItShouldReturnUnassociatedAndDeletableTraceDetailWithoutCheckingAssociations() {
        BddLogger.when("getting trace detail for an unassociated trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(traceRepository.isAssociated(List.of(trace))).thenReturn(Map.of(trace, false));

        TraceDetailData result = traceService.getTraceDetail(trace.getId());

        BddLogger.then("it should return a deletable trace detail");

        assertFalse(result.isAssociated());
        verify(declaredActivityService, never()).findAllDeclaredActivitiesByIds(anyList());
        verify(declaredActivityService, never()).getDeclaredActivityStatus(anyList());
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenTraceBelongsToAnotherUser() {
        BddLogger.when("getting trace detail of another user");

        Trace trace = TraceFixture.create().withUser(UserFixture.create().toModel()).toModel();

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));

        UserNotAuthorizedException exception =
            assertThrows(
                UserNotAuthorizedException.class, () -> traceService.getTraceDetail(trace.getId()));

        BddLogger.then("it should throw USER_NOT_AUTHORIZED");

        assertEquals(EErrorCode.USER_NOT_AUTHORIZED, exception.getErrorCode());
      }
    }

    @Nested
    class WhenGettingTraceAssociations {

      @Test
      void thenItShouldReturnDeclaredActivityAssociations() {
        BddLogger.when("getting trace associations");

        UUID traceId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

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

        TraceAssociationsData result = traceService.getTraceAssociations(traceId, false);

        BddLogger.then("it should return declared activity associations");

        assertEquals(1, result.declaredActivityAssociations().size());
      }

      @Test
      void thenItShouldCallFindAllNotCompletedWhenOnlyNotCompletedIsTrue() {
        BddLogger.when("getting only not completed trace associations");

        UUID traceId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        Association association = mock(Association.class);
        when(association.getAssociationType()).thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
        when(association.getId1()).thenReturn(activityId);

        DeclaredActivity activity = mock(DeclaredActivity.class);
        when(activity.getId()).thenReturn(activityId);

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(associationService.getAllOf(
                traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
            .thenReturn(List.of(association));
        when(declaredActivityService.findAllNotCompletedActivitiesByIds(List.of(activityId)))
            .thenReturn(List.of(activity));

        TraceAssociationsData result = traceService.getTraceAssociations(traceId, true);

        BddLogger.then("it should call the not completed method and return the data");

        verify(declaredActivityService).findAllNotCompletedActivitiesByIds(List.of(activityId));
        verify(declaredActivityService, never()).findAllDeclaredActivitiesByIds(any());
        assertEquals(1, result.declaredActivityAssociations().size());
      }

      @Test
      void thenItShouldThrowTraceNotFoundWhenTraceDoesNotExist() {
        BddLogger.when("getting trace associations for unknown trace");

        UUID traceId = UUID.randomUUID();

        when(traceRepository.findById(traceId)).thenReturn(Optional.empty());

        assertThrows(
            TraceNotFoundException.class, () -> traceService.getTraceAssociations(traceId, false));

        BddLogger.then("it should throw TraceNotFoundException");
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenTraceBelongsToAnotherUser() {
        BddLogger.when("getting trace associations of another user");

        UUID traceId = UUID.randomUUID();
        Trace trace =
            TraceFixture.create()
                .withUser(UserFixture.create().toModel())
                .withId(traceId)
                .toModel();

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));

        assertThrows(
            UserNotAuthorizedException.class,
            () -> traceService.getTraceAssociations(traceId, false));

        BddLogger.then("it should throw UserNotAuthorizedException");
      }
    }

    @Nested
    class WhenAssociatingTraceWithDeclaredSkill {

      @Test
      void thenItShouldCreateAssociation() {
        BddLogger.when("associating trace with declared skills");

        UUID traceId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);
        when(skill.getId()).thenReturn(skillId);
        when(skill.getStudent()).thenReturn(student);

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
            .thenReturn(List.of(skill));
        when(associationService.getAllOf(
                traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
            .thenReturn(List.of());

        TraceAssociationsData result =
            traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId));

        BddLogger.then("it should create association");

        verify(associationService).createAll(any());
        assertNotNull(result);
      }

      @Test
      void thenItShouldThrowDeclaredSkillProgressNotFoundWhenSkillIsMissing() {
        BddLogger.when("associating trace with missing declared skill");

        UUID traceId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
            .thenReturn(List.of());

        assertThrows(
            DeclaredSkillProgressNotFoundException.class,
            () -> traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId)));

        BddLogger.then("it should throw DeclaredSkillProgressNotFoundException");
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenSkillBelongsToAnotherUser() {
        BddLogger.when("associating trace with skill of another user");

        UUID traceId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        DeclaredSkillProgress skill = mock(DeclaredSkillProgress.class);
        when(skill.getId()).thenReturn(skillId);
        when(skill.getStudent()).thenReturn(StudentFixture.create().toModel());

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(declaredSkillProgressService.findAllDeclaredSkillProgressesByIds(List.of(skillId)))
            .thenReturn(List.of(skill));

        assertThrows(
            UserNotAuthorizedException.class,
            () -> traceService.associateTraceWithDeclaredSkill(traceId, List.of(skillId)));

        BddLogger.then("it should throw UserNotAuthorizedException");
      }
    }

    @Nested
    class WhenAssociatingTraceWithDeclaredExperience {

      @Test
      void thenItShouldCreateAssociation() {
        BddLogger.when("associating trace with declared experiences");

        UUID traceId = UUID.randomUUID();
        UUID experienceId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        DeclaredExperience experience = mock(DeclaredExperience.class);
        when(experience.getId()).thenReturn(experienceId);
        when(experience.getStudent()).thenReturn(student);

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(declaredExperienceService.findAllByIds(List.of(experienceId)))
            .thenReturn(List.of(experience));
        when(associationService.getAllOf(
                traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
            .thenReturn(List.of());

        TraceAssociationsData result =
            traceService.associateTraceWithDeclaredExperience(traceId, List.of(experienceId));

        BddLogger.then("it should create association");

        verify(associationService).createAll(any());
        assertNotNull(result);
      }

      @Test
      void thenItShouldThrowDeclaredExperienceNotFoundWhenExperienceIsMissing() {
        BddLogger.when("associating trace with missing declared experience");

        UUID traceId = UUID.randomUUID();
        UUID experienceId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(declaredExperienceService.findAllByIds(List.of(experienceId))).thenReturn(List.of());

        assertThrows(
            DeclaredExperienceNotFoundException.class,
            () ->
                traceService.associateTraceWithDeclaredExperience(traceId, List.of(experienceId)));

        BddLogger.then("it should throw DeclaredExperienceNotFoundException");
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenExperienceBelongsToAnotherUser() {
        BddLogger.when("associating trace with experience of another user");

        UUID traceId = UUID.randomUUID();
        UUID experienceId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();
        DeclaredExperience experience = Mockito.mock(DeclaredExperience.class);

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(declaredExperienceService.findAllByIds(List.of(experienceId)))
            .thenReturn(List.of(experience));
        when(experience.getId()).thenReturn(experienceId);
        when(experience.getStudent()).thenReturn(StudentFixture.create().toModel());

        assertThrows(
            UserNotAuthorizedException.class,
            () ->
                traceService.associateTraceWithDeclaredExperience(traceId, List.of(experienceId)));

        BddLogger.then("it should throw UserNotAuthorizedException");
      }
    }

    @Nested
    class WhenUnassociatingTrace {

      @Test
      void thenItShouldDeleteAssociationsWhenTheyBelongToTrace() {
        BddLogger.when("unassociating valid associations from trace");

        UUID traceId = UUID.randomUUID();
        UUID associationId1 = UUID.randomUUID();
        UUID associationId2 = UUID.randomUUID();
        List<UUID> idsToDelete = List.of(associationId1, associationId2);

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        Association association1 = mock(Association.class);
        when(association1.getId()).thenReturn(associationId1);

        Association association2 = mock(Association.class);
        when(association2.getId()).thenReturn(associationId2);

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(associationService.getAllOf(
                traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
            .thenReturn(List.of(association1, association2));

        traceService.unassociate(traceId, idsToDelete);

        BddLogger.then("it should delete associations");

        verify(associationService).deleteAllByIds(idsToDelete);
      }

      @Test
      void thenItShouldThrowAssociationDoesNotExistWhenIdsAreNotLinkedToTrace() {
        BddLogger.when("unassociating ids not linked to trace");

        UUID linkedAssociationId = UUID.randomUUID();
        UUID unlinkedAssociationId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();

        Association association = mock(Association.class);
        when(association.getId()).thenReturn(linkedAssociationId);

        when(traceRepository.findById(trace.getId())).thenReturn(Optional.of(trace));
        when(associationService.getAllOf(
                trace.getId(), Trace.class, EAssociationType.getAllBy(Trace.class)))
            .thenReturn(List.of(association));

        assertThrows(
            AssociationDoesNotExistException.class,
            () ->
                traceService.unassociate(
                    trace.getId(), List.of(linkedAssociationId, unlinkedAssociationId)));

        BddLogger.then("it should not delete associations");

        verify(associationService, never()).deleteAllByIds(any());
      }

      @Test
      void thenItShouldThrowDeclaredActivityAlreadyFinishedWhenActivityAssociationIsFinished() {
        BddLogger.when("unassociating a finished declared activity association");

        UUID traceId = UUID.randomUUID();
        UUID associationId = UUID.randomUUID();
        UUID activityId = UUID.randomUUID();

        Trace trace = TraceFixture.create().withUser(student.getUser()).withId(traceId).toModel();

        Association association = mock(Association.class);
        when(association.getId()).thenReturn(associationId);
        when(association.getAssociationType()).thenReturn(EAssociationType.DECLARED_ACTIVITY_TRACE);
        when(association.getId1()).thenReturn(activityId);

        DeclaredActivity activity = mock(DeclaredActivity.class);
        when(activity.getFinishedAt()).thenReturn(Optional.of(Instant.now()));

        when(traceRepository.findById(traceId)).thenReturn(Optional.of(trace));
        when(associationService.getAllOf(
                traceId, Trace.class, EAssociationType.getAllBy(Trace.class)))
            .thenReturn(List.of(association));
        when(declaredActivityService.findAllDeclaredActivitiesByIds(List.of(activityId)))
            .thenReturn(List.of(activity));

        assertThrows(
            DeclaredActivityAlreadyFinishedException.class,
            () -> traceService.unassociate(traceId, List.of(associationId)));

        BddLogger.then("it should not delete associations");

        verify(associationService, never()).deleteAllByIds(any());
      }
    }

    @Nested
    class WhenGettingLockedDeclaredActivities {

      @Test
      void thenItShouldReturnLockedDeclaredActivitiesGroupedByTrace() {
        BddLogger.when("getting locked declared activities for traces");

        Trace firstTrace = TraceFixture.create().withUser(student.getUser()).toModel();
        Trace secondTrace = TraceFixture.create().withUser(student.getUser()).toModel();

        UUID firstDeclaredActivityId = UUID.randomUUID();
        UUID secondDeclaredActivityId = UUID.randomUUID();

        DeclaredActivity firstDeclaredActivity =
            mockDeclaredActivityWithTitle(firstDeclaredActivityId, "Submitted activity");
        DeclaredActivity secondDeclaredActivity = mock(DeclaredActivity.class);

        List<UUID> traceIds = List.of(firstTrace.getId(), secondTrace.getId());

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(firstTrace, secondTrace));

        when(associationService.getAllOf(
                eq(traceIds),
                eq(Trace.class),
                eq(List.of(EAssociationType.DECLARED_ACTIVITY_TRACE))))
            .thenReturn(
                List.of(
                    Association.create(
                        firstDeclaredActivityId,
                        firstTrace.getId(),
                        EAssociationType.DECLARED_ACTIVITY_TRACE),
                    Association.create(
                        secondDeclaredActivityId,
                        secondTrace.getId(),
                        EAssociationType.DECLARED_ACTIVITY_TRACE)));

        when(declaredActivityService.findAllDeclaredActivitiesByIds(anyList()))
            .thenReturn(List.of(firstDeclaredActivity, secondDeclaredActivity));

        when(declaredActivityService.getDeclaredActivityStatus(
                List.of(firstDeclaredActivity, secondDeclaredActivity)))
            .thenReturn(
                Map.of(
                    firstDeclaredActivity, EDeclaredActivityStatus.SUBMITTED,
                    secondDeclaredActivity, EDeclaredActivityStatus.IN_PROGRESS));

        List<TraceLockedDeclaredActivitiesData> result =
            traceService.getLockedDeclaredActivities(traceIds);

        BddLogger.then("it should return only locked activities with trace title");

        assertEquals(2, result.size());

        TraceLockedDeclaredActivitiesData firstResult = result.getFirst();
        TraceLockedDeclaredActivitiesData secondResult = result.get(1);

        assertEquals(firstTrace.getId(), firstResult.traceId());
        assertEquals(firstTrace.getTitle(), firstResult.traceTitle());
        assertEquals(1, firstResult.lockedDeclaredActivities().size());
        assertEquals(
            firstDeclaredActivityId,
            firstResult.lockedDeclaredActivities().getFirst().activityId());
        assertEquals(
            "Submitted activity",
            firstResult.lockedDeclaredActivities().getFirst().activityTitle());
        assertEquals(
            EDeclaredActivityStatus.SUBMITTED,
            firstResult.lockedDeclaredActivities().getFirst().activityStatus());

        assertEquals(secondTrace.getId(), secondResult.traceId());
        assertEquals(secondTrace.getTitle(), secondResult.traceTitle());
        assertTrue(secondResult.lockedDeclaredActivities().isEmpty());
      }

      @Test
      void thenItShouldReturnEmptyResultWhenTraceIdsAreEmpty() {
        BddLogger.when("getting locked declared activities with empty trace ids");

        List<TraceLockedDeclaredActivitiesData> result =
            traceService.getLockedDeclaredActivities(List.of());

        BddLogger.then("it should return an empty list");

        assertTrue(result.isEmpty());
        verify(traceRepository, never()).findAllById(anyList());
        verify(associationService, never()).getAllOf(anyList(), any(), anyList());
      }

      @Test
      void thenItShouldReturnEmptyLockedActivitiesWhenNoDeclaredActivityIsAssociated() {
        BddLogger.when(
            "getting locked declared activities for traces without declared activity association");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
        List<UUID> traceIds = List.of(trace.getId());

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(trace));

        when(associationService.getAllOf(
                eq(traceIds),
                eq(Trace.class),
                eq(List.of(EAssociationType.DECLARED_ACTIVITY_TRACE))))
            .thenReturn(List.of());

        List<TraceLockedDeclaredActivitiesData> result =
            traceService.getLockedDeclaredActivities(traceIds);

        BddLogger.then("it should return trace data with empty locked activities");

        assertEquals(1, result.size());
        assertEquals(trace.getId(), result.getFirst().traceId());
        assertEquals(trace.getTitle(), result.getFirst().traceTitle());
        assertTrue(result.getFirst().lockedDeclaredActivities().isEmpty());

        verify(declaredActivityService, never()).findAllDeclaredActivitiesByIds(anyList());
        verify(declaredActivityService, never()).getDeclaredActivityStatus(anyList());
      }

      @Test
      void thenItShouldThrowTraceNotFoundWhenOneTraceDoesNotExist() {
        BddLogger.when("getting locked declared activities with one unknown trace");

        Trace trace = TraceFixture.create().withUser(student.getUser()).toModel();
        UUID missingTraceId = UUID.randomUUID();
        List<UUID> traceIds = List.of(trace.getId(), missingTraceId);

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(trace));

        assertThrows(
            TraceNotFoundException.class, () -> traceService.getLockedDeclaredActivities(traceIds));

        BddLogger.then("it should throw TraceNotFoundException");

        verify(associationService, never()).getAllOf(anyList(), any(), anyList());
      }

      @Test
      void thenItShouldThrowUserNotAuthorizedWhenOneTraceBelongsToAnotherUser() {
        BddLogger.when("getting locked declared activities with one trace owned by another user");

        Trace ownedTrace = TraceFixture.create().withUser(student.getUser()).toModel();
        Trace otherUserTrace =
            TraceFixture.create().withUser(UserFixture.create().toModel()).toModel();

        List<UUID> traceIds = List.of(ownedTrace.getId(), otherUserTrace.getId());

        when(traceRepository.findAllById(traceIds)).thenReturn(List.of(ownedTrace, otherUserTrace));

        assertThrows(
            UserNotAuthorizedException.class,
            () -> traceService.getLockedDeclaredActivities(traceIds));

        BddLogger.then("it should throw UserNotAuthorizedException");

        verify(associationService, never()).getAllOf(anyList(), any(), anyList());
      }
    }
  }

  private DeclaredActivity mockDeclaredActivityWithTitle(
      UUID declaredActivityId, String activityTitle) {
    Activity activity = mock(Activity.class);
    DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

    when(declaredActivity.getId()).thenReturn(declaredActivityId);
    when(declaredActivity.getActivity()).thenReturn(activity);
    when(activity.getTitle()).thenReturn(activityTitle);

    return declaredActivity;
  }

  private DeclaredActivity mockDeclaredActivity() {
    return mock(DeclaredActivity.class);
  }

  private boolean isLocked(EDeclaredActivityStatus status) {
    return status == EDeclaredActivityStatus.SUBMITTED
        || status == EDeclaredActivityStatus.COMPLETED;
  }
}
