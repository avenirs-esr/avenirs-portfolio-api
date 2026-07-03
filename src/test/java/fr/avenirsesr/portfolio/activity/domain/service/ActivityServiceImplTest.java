package fr.avenirsesr.portfolio.activity.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityUnpublishedException;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class ActivityServiceImplTest {

  @Mock private ActivityRepository activityRepository;
  @Mock private ActivityDraftRepository activityDraftRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private StaffActivityOverviewRepository staffActivityOverviewRepository;

  @InjectMocks private ActivityServiceImpl activityService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class GivenAnActivityService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an activity service");
    }

    @Nested
    class WhenCreatingAnActivity {

      UUID id;
      Staff author;
      List<String> links;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating an activity");
        id = UUID.randomUUID();
        author = mock(Staff.class);
        links = List.of("https://example.com");
      }

      @Test
      void thenItShouldReturnActivityAndSaveIt() {
        BddLogger.then("the activity should be created and saved");

        Activity createdActivity =
            activityService.create(
                id,
                author,
                "Test Activity",
                EActivityThematic.EXPERIENCES,
                "This is a test activity",
                "<h3>Objectives</h3><p>Test activity description</p>",
                "2026",
                "Short label",
                true,
                -1,
                -1,
                links);

        assertNotNull(createdActivity);
        assertEquals(id, createdActivity.getId());
        assertEquals("Test Activity", createdActivity.getTitle());
        assertEquals(EActivityThematic.EXPERIENCES, createdActivity.getThematic());
        assertEquals("This is a test activity", createdActivity.getSummary());
        assertEquals(
            "<h3>Objectives</h3><p>Test activity description</p>",
            createdActivity.getDescription().get());
        assertEquals("2026", createdActivity.getExecutionPeriodInfo().get());
        assertEquals(links, createdActivity.getLinks());

        ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
        verify(activityRepository).save(captor.capture());
        assertEquals(createdActivity, captor.getValue());
      }
    }

    @Nested
    class WhenCreatingAnActivityDraft {

      Staff staff;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("creating an activity draft");
        staff = mock(Staff.class);
        when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
        when(activityDraftRepository.save(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
      }

      @Test
      void thenItShouldReturnDraftAndSaveIt() {
        BddLogger.then("the draft should be created and saved");

        ActivityDraft result = activityService.createActivityDraft("Mon brouillon d'activité");

        assertNotNull(result);
        assertEquals("Mon brouillon d'activité", result.getTitle());
        assertEquals(staff, result.getAuthor());

        verify(loggedInUserService).getLoggedInStaff();
        verify(activityDraftRepository).save(result);
      }

      @Test
      void thenItShouldInitializeDefaultValues() {
        BddLogger.then("the draft should be initialized with default values");

        ActivityDraft result =
            activityService.createActivityDraft("Brouillon avec valeurs par défaut");

        assertEquals(EActivityThematic.TRANSVERSAL, result.getThematic());
        assertEquals(-1, result.getTraceAllowedAssociations());
        assertEquals(-1, result.getFeedbackAllowedIterations());
        assertTrue(result.isEnableReflection());
      }

      @Test
      void thenItShouldInitializeNullableFieldsAsEmptyOptionals() {
        BddLogger.then("nullable fields should be empty optionals");

        ActivityDraft result = activityService.createActivityDraft("Brouillon");

        assertTrue(result.getSummary().isEmpty());
        assertTrue(result.getDescription().isEmpty());
        assertTrue(result.getExecutionPeriodInfo().isEmpty());
        assertTrue(result.getExecutionPeriodInfoSummary().isEmpty());
      }
    }

    @Nested
    class WhenUpdatingAnActivityDraft {

      UUID draftId;
      Staff loggedInStaff;
      ActivityDraft draft;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("updating an activity draft");
        draftId = UUID.randomUUID();
        loggedInStaff = mock(Staff.class);
        draft = mock(ActivityDraft.class);
        when(loggedInUserService.getLoggedInStaff()).thenReturn(loggedInStaff);
      }

      @Nested
      class AndTheDraftExists {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft exists");
          when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
        }

        @Nested
        class AndTheLoggedInStaffIsTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is the author");
            when(draft.getAuthor()).thenReturn(loggedInStaff);
            when(activityDraftRepository.save(draft)).thenReturn(draft);
          }

          @Test
          void thenItShouldUpdateAllFieldsAndSave() {
            BddLogger.then("all provided fields should be updated and saved");

            List<String> links = List.of("https://example.com", "https://avenirs-esr.fr");
            ActivityDraft result =
                activityService.updateActivity(
                    EActivityStatus.DRAFT,
                    draftId,
                    "Nouveau titre",
                    EActivityThematic.EXPERIENCES,
                    "Nouveau summary",
                    "<p>Nouvelle description</p>",
                    "Avant entretien",
                    "Label court",
                    5,
                    3,
                    false,
                    links);

            verify(draft).setTitle("Nouveau titre");
            verify(draft).setThematic(EActivityThematic.EXPERIENCES);
            verify(draft).setSummary("Nouveau summary");
            verify(draft).setDescription("<p>Nouvelle description</p>");
            verify(draft).setExecutionPeriodInfo("Avant entretien");
            verify(draft).setExecutionPeriodInfoSummary("Label court");
            verify(draft).setTraceAllowedAssociations(5);
            verify(draft).setFeedbackAllowedIterations(3);
            verify(draft).setEnableReflection(false);
            verify(draft).addLinks(links);
            verify(activityDraftRepository).save(draft);
            assertEquals(draft, result);
          }

          @Test
          void thenItShouldNotUpdateFieldsWhenNullIsPassed() {
            BddLogger.then("no field should be updated when null values are passed");

            activityService.updateActivity(
                EActivityStatus.DRAFT,
                draftId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

            verify(draft, never()).setTitle(any());
            verify(draft, never()).setThematic(any());
            verify(draft, never()).setSummary(any());
            verify(draft, never()).setDescription(any());
            verify(draft, never()).setExecutionPeriodInfo(any());
            verify(draft, never()).setExecutionPeriodInfoSummary(any());
            verify(draft, never()).setTraceAllowedAssociations(anyInt());
            verify(draft, never()).setFeedbackAllowedIterations(anyInt());
            verify(draft, never()).setEnableReflection(anyBoolean());
            verify(draft, never()).addLinks(anyList());
            verify(activityDraftRepository).save(draft);
          }

          @Test
          void thenItShouldOnlyUpdateProvidedFields() {
            BddLogger.then("only provided fields should be updated");

            activityService.updateActivity(
                EActivityStatus.DRAFT,
                draftId,
                "Titre seul",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

            verify(draft).setTitle("Titre seul");
            verify(draft, never()).setThematic(any());
            verify(draft, never()).setSummary(any());
            verify(draft, never()).setDescription(any());
            verify(draft, never()).setExecutionPeriodInfo(any());
            verify(draft, never()).setExecutionPeriodInfoSummary(any());
            verify(draft, never()).setTraceAllowedAssociations(anyInt());
            verify(draft, never()).setFeedbackAllowedIterations(anyInt());
            verify(draft, never()).setEnableReflection(anyBoolean());
            verify(draft, never()).addLinks(anyList());
          }

          @Test
          void thenItShouldReturnSavedDraft() {
            BddLogger.then("the saved draft should be returned");

            ActivityDraft savedDraft = mock(ActivityDraft.class);
            when(activityDraftRepository.save(draft)).thenReturn(savedDraft);

            ActivityDraft result =
                activityService.updateActivity(
                    EActivityStatus.DRAFT,
                    draftId,
                    "Titre",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            assertEquals(savedDraft, result);
          }
        }

        @Nested
        class AndTheLoggedInStaffIsNotTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is not the author");
            when(draft.getAuthor()).thenReturn(mock(Staff.class));
          }

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.then("the service should throw UserNotAuthorizedException");

            assertThrows(
                UserNotAuthorizedException.class,
                () ->
                    activityService.updateActivity(
                        EActivityStatus.DRAFT,
                        draftId,
                        "Titre",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));

            verify(activityDraftRepository, never()).save(any());
          }
        }
      }

      @Nested
      class AndTheDraftDoesNotExist {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft does not exist");
          when(activityDraftRepository.findById(draftId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowActivityDraftNotFoundException() {
          BddLogger.then("the service should throw ActivityDraftNotFoundException");

          assertThrows(
              ActivityDraftNotFoundException.class,
              () ->
                  activityService.updateActivity(
                      EActivityStatus.DRAFT,
                      draftId,
                      "Titre",
                      null,
                      null,
                      null,
                      null,
                      null,
                      null,
                      null,
                      null,
                      null));

          verify(activityDraftRepository, never()).save(any());
        }
      }
    }

    @Nested
    class WhenPublishingAnActivityDraft {

      UUID draftId;
      Staff loggedInStaff;
      ActivityDraft draft;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("publishing an activity draft");
        draftId = UUID.randomUUID();
        loggedInStaff = mock(Staff.class);
        draft = mock(ActivityDraft.class);
        when(loggedInUserService.getLoggedInStaff()).thenReturn(loggedInStaff);
      }

      @Nested
      class AndTheDraftExists {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft exists");
          when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
        }

        @Nested
        class AndTheLoggedInStaffIsTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is the author");
            mockPublishableDraft(draft, draftId, loggedInStaff, List.of("https://example.com"));
            when(activityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
          }

          @Test
          void thenItShouldCreateActivityFromDraftAndSaveIt() {
            BddLogger.then("an activity should be created from the draft and saved");

            Activity result = activityService.publish(draftId);

            assertNotNull(result);
            assertEquals(draftId, result.getId());
            assertEquals("Mon activité", result.getTitle());
            assertEquals(EActivityThematic.EXPERIENCES, result.getThematic());
            assertEquals("Un résumé", result.getSummary());
            assertEquals(List.of("https://example.com"), result.getLinks());

            verify(activityRepository).save(any(Activity.class));
            verify(activityDraftRepository).removeFromDatabase(draft);
          }

          @Test
          void thenItShouldRemoveDraftAfterPublishing() {
            BddLogger.then("the draft should be removed after publishing");

            activityService.publish(draftId);

            InOrder inOrder = inOrder(activityRepository, activityDraftRepository);
            inOrder.verify(activityRepository).save(any(Activity.class));
            inOrder.verify(activityDraftRepository).removeFromDatabase(draft);
          }

          @Test
          void thenItShouldMapOptionalFieldsAsEmptyOptionalsWhenNotPresent() {
            BddLogger.then("optional fields should be empty when absent from draft");

            when(draft.getDescription()).thenReturn(Optional.empty());
            when(draft.getExecutionPeriodInfo()).thenReturn(Optional.empty());
            when(draft.getExecutionPeriodInfoSummary()).thenReturn(Optional.empty());

            Activity result = activityService.publish(draftId);

            assertNotNull(result);
            assertTrue(result.getDescription().isEmpty());
            assertTrue(result.getExecutionPeriodInfo().isEmpty());
            assertTrue(result.getExecutionPeriodInfoSummary().isEmpty());
          }

          @Test
          void thenItShouldPreserveAllDraftFields() {
            BddLogger.then("all draft fields should be preserved");

            List<String> links = List.of("https://example.com", "https://avenirs-esr.fr");
            when(draft.getTitle()).thenReturn("Titre complet");
            when(draft.getThematic()).thenReturn(EActivityThematic.SELF_KNOWLEDGE);
            when(draft.getSummary()).thenReturn(Optional.of("Résumé complet"));
            when(draft.getDescription()).thenReturn(Optional.of("<p>Description complète</p>"));
            when(draft.getExecutionPeriodInfo()).thenReturn(Optional.of("Avant entretien"));
            when(draft.getExecutionPeriodInfoSummary()).thenReturn(Optional.of("Label court"));
            when(draft.isEnableReflection()).thenReturn(false);
            when(draft.getTraceAllowedAssociations()).thenReturn(3);
            when(draft.getFeedbackAllowedIterations()).thenReturn(5);
            when(draft.getLinks()).thenReturn(links);

            Activity result = activityService.publish(draftId);

            assertEquals(draftId, result.getId());
            assertEquals("Titre complet", result.getTitle());
            assertEquals(EActivityThematic.SELF_KNOWLEDGE, result.getThematic());
            assertEquals("Résumé complet", result.getSummary());
            assertEquals("<p>Description complète</p>", result.getDescription().get());
            assertEquals("Avant entretien", result.getExecutionPeriodInfo().get());
            assertEquals("Label court", result.getExecutionPeriodInfoSummary().get());
            assertFalse(result.isEnableReflection());
            assertEquals(3, result.getTraceAllowedAssociations());
            assertEquals(5, result.getFeedbackAllowedIterations());
            assertEquals(links, result.getLinks());
          }

          @Test
          void thenItShouldThrowFieldValidationExceptionWhenSummaryIsEmpty() {
            BddLogger.then("the service should throw FieldValidationException");

            when(draft.getSummary()).thenReturn(Optional.empty());

            assertThrows(FieldValidationException.class, () -> activityService.publish(draftId));

            verify(activityRepository, never()).save(any());
            verify(activityDraftRepository, never()).removeFromDatabase(any());
          }
        }

        @Nested
        class AndTheLoggedInStaffIsNotTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is not the author");
            when(draft.getAuthor()).thenReturn(mock(Staff.class));
          }

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.then("the service should throw UserNotAuthorizedException");

            assertThrows(UserNotAuthorizedException.class, () -> activityService.publish(draftId));

            verify(activityRepository, never()).save(any());
            verify(activityDraftRepository, never()).removeFromDatabase(any());
          }
        }
      }

      @Nested
      class AndTheDraftDoesNotExist {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft does not exist");
          when(activityDraftRepository.findById(draftId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowActivityDraftNotFoundException() {
          BddLogger.then("the service should throw ActivityDraftNotFoundException");

          assertThrows(
              ActivityDraftNotFoundException.class, () -> activityService.publish(draftId));

          verify(activityRepository, never()).save(any());
          verify(activityDraftRepository, never()).removeFromDatabase(any());
        }
      }
    }

    @Nested
    class WhenUnpublishingAnActivity {

      UUID activityId;
      Staff loggedInStaff;
      Activity activity;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("unpublishing an activity");
        activityId = UUID.randomUUID();
        loggedInStaff = mock(Staff.class);
        activity = mock(Activity.class);
        when(loggedInUserService.getLoggedInStaff()).thenReturn(loggedInStaff);
      }

      @Nested
      class AndActivityExists {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity exists");
          when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
        }

        @Nested
        class AndTheLoggedInStaffIsTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is the author");
            when(activity.getAuthor()).thenReturn(loggedInStaff);
            when(activityRepository.save(activity)).thenReturn(activity);
          }

          @Test
          void thenItShouldSetStatusToUnpublished() {
            BddLogger.then("the activity status should be set to UNPUBLISHED");

            activityService.unpublish(activityId);

            verify(activity).setStatus(EActivityStatus.UNPUBLISHED);
          }

          @Test
          void thenItShouldSaveActivity() {
            BddLogger.then("the activity should be saved");

            activityService.unpublish(activityId);

            verify(activityRepository).save(activity);
          }

          @Test
          void thenItShouldReturnSavedActivity() {
            BddLogger.then("the saved activity should be returned");

            Activity result = activityService.unpublish(activityId);

            assertEquals(activity, result);
          }
        }

        @Nested
        class AndTheLoggedInStaffIsNotTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is not the author");
            when(activity.getAuthor()).thenReturn(mock(Staff.class));
          }

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.then("the service should throw UserNotAuthorizedException");

            assertThrows(
                UserNotAuthorizedException.class, () -> activityService.unpublish(activityId));

            verify(activity, never()).setStatus(any());
            verify(activityRepository, never()).save(any());
          }
        }

        @Nested
        class AndTheActivityIsAlreadyUnpublished {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the activity is already unpublished");
            when(activity.getAuthor()).thenReturn(loggedInStaff);
            when(activity.getStatus()).thenReturn(EActivityStatus.UNPUBLISHED);
          }

          @Test
          void thenItShouldThrowActivityUnpublishedException() {
            BddLogger.then("the service should throw ActivityUnpublishedException");

            assertThrows(
                ActivityUnpublishedException.class, () -> activityService.unpublish(activityId));

            verify(activity, never()).setStatus(any());
            verify(activityRepository, never()).save(any());
          }
        }
      }

      @Nested
      class AndActivityDoesNotExist {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity does not exist");
          when(activityRepository.findById(activityId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowActivityNotFoundException() {
          BddLogger.then("the service should throw ActivityNotFoundException");

          assertThrows(
              ActivityNotFoundException.class, () -> activityService.unpublish(activityId));

          verify(activityRepository, never()).save(any());
        }
      }
    }

    @Nested
    class WhenDeletingAnActivityDraft {

      UUID draftId;
      ActivityDraft draft;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting an activity draft");
        draftId = UUID.randomUUID();
        draft = mock(ActivityDraft.class);
      }

      @Nested
      class AndTheDraftExists {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft exists");
          when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
        }

        @Nested
        class AndTheLoggedInStaffIsTheAuthor {

          Staff staff;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is the author of the draft");
            staff = mock(Staff.class);
            when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
            when(draft.getAuthor()).thenReturn(staff);
          }

          @Test
          void thenItShouldBeDeleted() {
            BddLogger.then("the draft should be deleted");

            activityService.deleteDraft(draftId);

            verify(activityDraftRepository).removeFromDatabase(draft);
          }
        }

        @Nested
        class AndTheLoggedInStaffIsNotTheAuthor {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is not the author of the draft");
            when(loggedInUserService.getLoggedInStaff()).thenReturn(mock(Staff.class));
            when(draft.getAuthor()).thenReturn(mock(Staff.class));
          }

          @Test
          void thenItShouldThrowUserNotAuthorizedException() {
            BddLogger.then("the service should throw UserNotAuthorizedException");

            assertThrows(
                UserNotAuthorizedException.class, () -> activityService.deleteDraft(draftId));

            verify(activityDraftRepository, never()).removeFromDatabase(any());
          }
        }
      }

      @Nested
      class AndTheDraftDoesNotExist {

        UUID unknownId;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft does not exist");
          unknownId = UUID.randomUUID();
          when(activityDraftRepository.findById(unknownId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowActivityDraftNotFoundException() {
          BddLogger.then("the service should throw ActivityDraftNotFoundException");

          assertThrows(
              ActivityDraftNotFoundException.class, () -> activityService.deleteDraft(unknownId));

          verify(activityDraftRepository, never()).removeFromDatabase(any());
        }
      }
    }

    @Nested
    class WhenGettingActivityNavigation {

      Staff author;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting activity navigation");
        author = mock(Staff.class);
      }

      @Test
      void thenItShouldGroupPublishedActivitiesByThematic() {
        BddLogger.then("published activities should be grouped by thematic");

        Activity a1 = createPublishedActivity("A1", EActivityThematic.EXPERIENCES);
        Activity a2 = createPublishedActivity("A2", EActivityThematic.EXPERIENCES);
        Activity a3 = createPublishedActivity("A3", EActivityThematic.RESUMES);

        when(activityRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(EActivityThematic.EXPERIENCES).size());
        assertEquals(a3, result.get(EActivityThematic.RESUMES).getFirst());

        verify(activityRepository).findAll();
        verifyNoMoreInteractions(activityRepository);
      }

      @Test
      void thenItShouldExcludeUnpublishedActivities() {
        BddLogger.then("unpublished activities should be excluded");

        Activity published = createPublishedActivity("Published", EActivityThematic.EXPERIENCES);
        Activity unpublished =
            Activity.toDomain(
                UUID.randomUUID(),
                author,
                "Unpublished",
                EActivityThematic.EXPERIENCES,
                "Summary",
                EActivityStatus.UNPUBLISHED,
                "Description",
                "2026",
                null,
                true,
                -1,
                -1,
                null,
                List.of(),
                Instant.now(),
                Instant.now());

        when(activityRepository.findAll()).thenReturn(List.of(published, unpublished));

        Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

        assertEquals(1, result.get(EActivityThematic.EXPERIENCES).size());
        assertTrue(result.get(EActivityThematic.EXPERIENCES).contains(published));
        assertFalse(result.get(EActivityThematic.EXPERIENCES).contains(unpublished));
      }

      @Test
      void thenItShouldNotIncludeThematicsThatAreNotPresent() {
        BddLogger.then("the result should not include absent thematics");

        Activity activity = createPublishedActivity("A1", EActivityThematic.RESUMES);
        when(activityRepository.findAll()).thenReturn(List.of(activity));

        Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(EActivityThematic.RESUMES));
        assertFalse(result.containsKey(EActivityThematic.EXPERIENCES));
        assertFalse(result.containsKey(EActivityThematic.SELF_KNOWLEDGE));
      }

      @Test
      void thenItShouldReturnEmptyMapWhenNoActivitiesExist() {
        BddLogger.then("an empty map should be returned");

        when(activityRepository.findAll()).thenReturn(List.of());

        Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

        assertNotNull(result);
        assertTrue(result.isEmpty());
      }

      private Activity createPublishedActivity(String title, EActivityThematic thematic) {
        return Activity.create(
            UUID.randomUUID(),
            author,
            title,
            thematic,
            "Summary",
            "Description",
            "2026",
            null,
            true,
            -1,
            -1,
            null,
            List.of());
      }
    }

    @Nested
    class WhenGettingActivitiesView {

      PageCriteria pageCriteria;
      PageInfo pageInfo;
      Student student;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting activities view");
        pageCriteria = mock(PageCriteria.class);
        pageInfo = new PageInfo(0, 10, 1);
        student = mock(Student.class);
        when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      }

      @Test
      void thenItShouldReturnActivitiesWithNullStatusWhenNotDeclared() {
        BddLogger.then("activities should have null status when not declared");

        Activity activity = mock(Activity.class);
        when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(10)));
        when(activityRepository.findAll(EActivityThematic.EXPERIENCES, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(activity), pageInfo));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

        var result = activityService.activitiesView(EActivityThematic.EXPERIENCES, pageCriteria);

        assertEquals(1, result.content().size());
        assertEquals(pageInfo, result.pageInfo());
        assertEquals(activity, result.content().getFirst().activity());
        assertNull(result.content().getFirst().status());
      }

      @Test
      void thenItShouldReturnActivitiesWithStudentStatus() {
        BddLogger.then("activities should include student status");

        Activity activity = mock(Activity.class);
        DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

        when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(10)));
        when(activityRepository.findAll(null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(activity), pageInfo));
        when(declaredActivity.getActivity()).thenReturn(activity);
        when(declaredActivityService.getAllDeclaredActivitiesOf(student))
            .thenReturn(List.of(declaredActivity));
        when(declaredActivityService.getDeclaredActivityStatus(List.of(declaredActivity)))
            .thenReturn(Map.of(declaredActivity, EDeclaredActivityStatus.SUBSCRIBED));

        var result = activityService.activitiesView(null, pageCriteria);

        assertEquals(1, result.content().size());
        assertEquals(activity, result.content().getFirst().activity());
        assertEquals(EDeclaredActivityStatus.SUBSCRIBED, result.content().getFirst().status());
      }

      @Test
      void thenItShouldMarkActivityAsNewWhenCreatedWithinLastThreeMonths() {
        BddLogger.then("the activity should be marked as new");

        Activity activity = mock(Activity.class);
        when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(10)));
        when(activityRepository.findAll(null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(activity), pageInfo));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

        var result = activityService.activitiesView(null, pageCriteria);

        assertTrue(result.content().getFirst().isNew());
      }

      @Test
      void thenItShouldMarkActivityAsNotNewWhenOlderThanThreeMonths() {
        BddLogger.then("the activity should not be marked as new");

        Activity activity = mock(Activity.class);
        when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(120)));
        when(activityRepository.findAll(null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(activity), pageInfo));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

        var result = activityService.activitiesView(null, pageCriteria);

        assertFalse(result.content().getFirst().isNew());
      }
    }

    @Nested
    class WhenGettingLatestActivitiesView {

      PageCriteria pageCriteria;
      PageInfo pageInfo;
      Student student;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting latest activities view");
        pageCriteria = mock(PageCriteria.class);
        pageInfo = new PageInfo(0, 10, 0);
        student = mock(Student.class);
        when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      }

      @Test
      void thenItShouldReturnEmptyWhenNoActivitiesExist() {
        BddLogger.then("an empty paged result should be returned");

        when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
            .thenReturn(new PagedResult<>(List.of(), pageInfo));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

        var result = activityService.latestActivitiesView(pageCriteria);

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(pageInfo, result.pageInfo());
      }

      @Test
      void thenItShouldReturnSingleActivity() {
        BddLogger.then("a single latest activity should be returned");

        Activity activity = mock(Activity.class);
        when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
            .thenReturn(new PagedResult<>(List.of(activity), new PageInfo(0, 10, 1)));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

        var result = activityService.latestActivitiesView(pageCriteria);

        assertEquals(1, result.content().size());
        assertEquals(activity, result.content().getFirst().activity());
        assertTrue(result.content().getFirst().isNew());
        assertNull(result.content().getFirst().status());
      }

      @Test
      void thenItShouldReturnMultipleActivities() {
        BddLogger.then("multiple latest activities should be returned");

        Activity activity1 = mock(Activity.class);
        Activity activity2 = mock(Activity.class);

        when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
            .thenReturn(new PagedResult<>(List.of(activity1, activity2), new PageInfo(0, 10, 2)));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

        var result = activityService.latestActivitiesView(pageCriteria);

        assertEquals(2, result.content().size());
        var activities = result.content().stream().map(a -> a.activity()).toList();
        assertTrue(activities.contains(activity1));
        assertTrue(activities.contains(activity2));
      }

      @Test
      void thenItShouldForwardDeclaredActivitiesToRepositoryForExclusion() {
        BddLogger.then("declared activities should be forwarded to the repository for exclusion");

        Activity activity1 = mock(Activity.class);
        Activity activity2 = mock(Activity.class);
        DeclaredActivity declaredActivity = mock(DeclaredActivity.class);

        when(declaredActivity.getActivity()).thenReturn(activity2);
        when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
            .thenReturn(new PagedResult<>(List.of(activity1, activity2), new PageInfo(0, 10, 2)));
        when(declaredActivityService.getAllDeclaredActivitiesOf(student))
            .thenReturn(List.of(declaredActivity));

        var result = activityService.latestActivitiesView(pageCriteria);

        assertEquals(2, result.content().size());
        assertTrue(result.content().stream().anyMatch(a -> a.activity() == activity1));
        assertTrue(result.content().stream().anyMatch(a -> a.activity() == activity2));
      }
    }

    @Nested
    class WhenGettingActivityPresentation {

      UUID activityId;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting activity presentation");
        activityId = UUID.randomUUID();
      }

      @Nested
      class AndTheActivityIsPublished {

        @Test
        void thenItShouldReturnActivityPresentationWhenActivityExists() {
          BddLogger.then("the activity presentation should be returned");

          UUID bannerId = UUID.randomUUID();
          Activity activity = mock(Activity.class);
          File banner = mock(File.class);

          when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
          when(activity.getBanner()).thenReturn(Optional.of(banner));
          when(declaredActivityService.getByActivity(activity)).thenReturn(Optional.empty());
          when(activity.getId()).thenReturn(activityId);
          when(activity.getTitle()).thenReturn("Activity");
          when(activity.getThematic()).thenReturn(EActivityThematic.EXPERIENCES);
          when(activity.getSummary()).thenReturn("is a test activity");
          when(activity.getDescription())
              .thenReturn(Optional.of("<h3>Objectives</h3><p>Test activity description</p>"));
          when(activity.getExecutionPeriodInfo()).thenReturn(Optional.of("2026"));
          when(activity.getCreatedAt()).thenReturn(Instant.now());
          when(activity.getUpdatedAt()).thenReturn(Instant.now());
          when(banner.getId()).thenReturn(bannerId);
          when(banner.getFileName()).thenReturn("filename.png");

          ActivityPresentationData result =
              activityService.getActivityPresentation(EActivityStatus.PUBLISHED, activityId);

          assertNotNull(result);
          assertEquals(activityId, result.id());
          assertEquals("Activity", result.title());
          assertEquals(EActivityThematic.EXPERIENCES, result.thematic());
          assertEquals("is a test activity", result.summary());
          assertEquals("<h3>Objectives</h3><p>Test activity description</p>", result.description());
          assertEquals("2026", result.executionPeriodInfo());
          assertTrue(result.banner().id().isPresent());
          assertEquals(bannerId, result.banner().id().get());
          assertEquals("filename.png", result.banner().name().get());
        }

        @Test
        void thenItShouldThrowActivityNotFoundExceptionWhenActivityDoesNotExist() {
          BddLogger.then("the service should throw ActivityNotFoundException");

          when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

          assertThrows(
              ActivityNotFoundException.class,
              () -> activityService.getActivityPresentation(EActivityStatus.PUBLISHED, activityId));

          verify(activityRepository).findById(activityId);
        }
      }
    }

    @Nested
    class WhenGettingStaffActivityWorkingSpace {

      PageCriteria pageCriteria;
      PageInfo pageInfo;
      Staff staff;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting staff activity working space");
        pageCriteria = mock(PageCriteria.class);
        pageInfo = new PageInfo(0, 8, 1);
        staff = mock(Staff.class);
        when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
      }

      @Test
      void thenItShouldReturnPagedResult() {
        BddLogger.then("a paged result should be returned");

        ActivityStaffOverviewData overviewData =
            new ActivityStaffOverviewData(
                UUID.randomUUID(),
                "Mon activité",
                EActivityThematic.EXPERIENCES,
                staff,
                EActivityStatus.PUBLISHED,
                Instant.now());

        when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(overviewData), pageInfo));

        var result = activityService.staffActivityWorkingSpace(pageCriteria);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(pageInfo, result.pageInfo());
        assertEquals(overviewData.activityId(), result.content().getFirst().activityId());
      }

      @Test
      void thenItShouldReturnEmptyWhenNoActivitiesExist() {
        BddLogger.then("an empty paged result should be returned");

        when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

        var result = activityService.staffActivityWorkingSpace(pageCriteria);

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
      }

      @Test
      void thenItShouldReturnMultipleActivities() {
        BddLogger.then("multiple activities should be returned");

        ActivityStaffOverviewData draft =
            new ActivityStaffOverviewData(
                UUID.randomUUID(),
                "Brouillon",
                EActivityThematic.RESUMES,
                staff,
                EActivityStatus.DRAFT,
                Instant.now());
        ActivityStaffOverviewData published =
            new ActivityStaffOverviewData(
                UUID.randomUUID(),
                "Publiée",
                EActivityThematic.EXPERIENCES,
                staff,
                EActivityStatus.PUBLISHED,
                Instant.now().minusSeconds(3600));
        ActivityStaffOverviewData unpublished =
            new ActivityStaffOverviewData(
                UUID.randomUUID(),
                "Dépubliée",
                EActivityThematic.SELF_KNOWLEDGE,
                staff,
                EActivityStatus.UNPUBLISHED,
                Instant.now().minusSeconds(7200));

        when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
            .thenReturn(
                new PagedResult<>(List.of(draft, published, unpublished), new PageInfo(0, 8, 3)));

        var result = activityService.staffActivityWorkingSpace(pageCriteria);

        assertEquals(3, result.content().size());
        var statuses =
            result.content().stream().map(ActivityStaffOverviewData::activityStatus).toList();
        assertTrue(statuses.contains(EActivityStatus.DRAFT));
        assertTrue(statuses.contains(EActivityStatus.PUBLISHED));
        assertTrue(statuses.contains(EActivityStatus.UNPUBLISHED));
      }

      @Test
      void thenItShouldUseLoggedInStaffAsFilter() {
        BddLogger.then("the logged-in staff should be used as repository filter");

        Staff otherStaff = mock(Staff.class);
        when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

        activityService.staffActivityWorkingSpace(pageCriteria);

        verify(staffActivityOverviewRepository).findAllByAuthor(staff, pageCriteria);
        verify(staffActivityOverviewRepository, never()).findAllByAuthor(eq(otherStaff), any());
      }

      @Test
      void thenItShouldMapAllFieldsCorrectly() {
        BddLogger.then("all fields should be mapped without transformation");

        UUID expectedId = UUID.randomUUID();
        Instant expectedUpdatedAt = Instant.parse("2024-01-15T10:00:00Z");

        ActivityStaffOverviewData overviewData =
            new ActivityStaffOverviewData(
                expectedId,
                "Titre précis",
                EActivityThematic.SELF_KNOWLEDGE,
                staff,
                EActivityStatus.DRAFT,
                expectedUpdatedAt);

        when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(overviewData), pageInfo));

        var result = activityService.staffActivityWorkingSpace(pageCriteria);
        var data = result.content().getFirst();

        assertEquals(expectedId, data.activityId());
        assertEquals("Titre précis", data.title());
        assertEquals(EActivityThematic.SELF_KNOWLEDGE, data.thematic());
        assertEquals(staff, data.author());
        assertEquals(EActivityStatus.DRAFT, data.activityStatus());
        assertEquals(expectedUpdatedAt, data.updatedAt());
      }
    }

    @Nested
    class WhenGettingStaffActivityLibrary {

      PageCriteria pageCriteria;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("getting staff activity library");
        pageCriteria = mock(PageCriteria.class);
      }

      @Test
      void thenItShouldReturnPagedResult() {
        BddLogger.then("a paged result should be returned");

        Staff staff = mock(Staff.class);
        PageInfo pageInfo = new PageInfo(0, 8, 1);
        ActivityStaffOverviewData overviewData =
            new ActivityStaffOverviewData(
                UUID.randomUUID(),
                "Mon activité",
                EActivityThematic.EXPERIENCES,
                staff,
                EActivityStatus.PUBLISHED,
                Instant.now());

        when(activityRepository.findAllStaffOverview(null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(overviewData), pageInfo));

        var result = activityService.staffActivityLibrary(null, pageCriteria);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(pageInfo, result.pageInfo());
        assertEquals(overviewData.activityId(), result.content().getFirst().activityId());

        verify(loggedInUserService).getLoggedInStaff();
        verify(activityRepository).findAllStaffOverview(null, pageCriteria);
      }

      @Test
      void thenItShouldReturnEmptyWhenNoActivitiesExist() {
        BddLogger.then("an empty paged result should be returned");

        PageInfo pageInfo = new PageInfo(0, 8, 0);
        when(activityRepository.findAllStaffOverview(null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(), pageInfo));

        var result = activityService.staffActivityLibrary(null, pageCriteria);

        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(pageInfo, result.pageInfo());

        verify(loggedInUserService).getLoggedInStaff();
        verify(activityRepository).findAllStaffOverview(null, pageCriteria);
      }

      @Test
      void thenItShouldForwardThematicToRepository() {
        BddLogger.then("the thematic should be forwarded to the repository");

        when(activityRepository.findAllStaffOverview(EActivityThematic.EXPERIENCES, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

        activityService.staffActivityLibrary(EActivityThematic.EXPERIENCES, pageCriteria);

        verify(loggedInUserService).getLoggedInStaff();
        verify(activityRepository)
            .findAllStaffOverview(EActivityThematic.EXPERIENCES, pageCriteria);
      }
    }
  }

  private void mockPublishableDraft(
      ActivityDraft draft, UUID draftId, Staff staff, List<String> links) {
    when(draft.getAuthor()).thenReturn(staff);
    when(draft.getId()).thenReturn(draftId);
    when(draft.getTitle()).thenReturn("Mon activité");
    when(draft.getThematic()).thenReturn(EActivityThematic.EXPERIENCES);
    when(draft.getSummary()).thenReturn(Optional.of("Un résumé"));
    when(draft.getDescription()).thenReturn(Optional.of("<p>Description</p>"));
    when(draft.getExecutionPeriodInfo()).thenReturn(Optional.of("2026"));
    when(draft.getExecutionPeriodInfoSummary()).thenReturn(Optional.empty());
    when(draft.isEnableReflection()).thenReturn(true);
    when(draft.getTraceAllowedAssociations()).thenReturn(-1);
    when(draft.getFeedbackAllowedIterations()).thenReturn(-1);
    when(draft.getBanner()).thenReturn(Optional.empty());
    when(draft.getLinks()).thenReturn(links);
  }
}
