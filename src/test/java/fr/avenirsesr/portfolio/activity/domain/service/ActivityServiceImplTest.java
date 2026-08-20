package fr.avenirsesr.portfolio.activity.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.error.domain.exception.FieldValidationException;
import fr.avenirsesr.portfolio.common.security.domain.exception.UserNotAuthorizedException;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.exception.FileTypeNotSupportedException;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.notification.domain.model.notification.ActivityUpdatedNotification;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.ActivityModifiedParameters;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.staff.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityNotFoundException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityUnpublishedException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityUpdatableField;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.service.ActivityServiceImpl;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
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
  @Mock private NotificationService notificationService;
  @Mock private FileResourceService fileResourceService;

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
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2030-06-30"),
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
            createdActivity.getDescription());
        assertEquals("2026", createdActivity.getRecommendedCompletionContexts().get());
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
        assertTrue(result.getRecommendedCompletionContexts().isEmpty());
        assertTrue(result.getStartDate().isEmpty());
        assertTrue(result.getEndDate().isEmpty());
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
                activityService.updateActivityDraft(
                    draftId,
                    "Nouveau titre",
                    EActivityThematic.EXPERIENCES,
                    "Nouveau summary",
                    "<p>Nouvelle description</p>",
                    "Avant entretien",
                    LocalDate.parse("2026-06-01"),
                    LocalDate.parse("2030-06-30"),
                    5,
                    3,
                    false,
                    links);

            verify(draft).setTitle("Nouveau titre");
            verify(draft).setThematic(EActivityThematic.EXPERIENCES);
            verify(draft).setSummary("Nouveau summary");
            verify(draft).setDescription("<p>Nouvelle description</p>");
            verify(draft).setRecommendedCompletionContexts("Avant entretien");
            verify(draft).setStartDate(LocalDate.parse("2026-06-01"));
            verify(draft).setEndDate(LocalDate.parse("2030-06-30"));
            verify(draft).setTraceAllowedAssociations(5);
            verify(draft).setFeedbackAllowedIterations(3);
            verify(draft).setEnableReflection(false);
            verify(draft).setLinks(links);
            verify(activityDraftRepository).save(draft);
            assertEquals(draft, result);
          }

          @Test
          void thenItShouldNotUpdateFieldsWhenNullIsPassed() {
            BddLogger.then("no field should be updated when null values are passed");

            activityService.updateActivityDraft(
                draftId, null, null, null, null, null, null, null, null, null, null, null);

            verify(draft, never()).setTitle(any());
            verify(draft, never()).setThematic(any());
            verify(draft, never()).setSummary(any());
            verify(draft, never()).setDescription(any());
            verify(draft, never()).setRecommendedCompletionContexts(any());
            verify(draft, never()).setStartDate(any());
            verify(draft, never()).setEndDate(any());
            verify(draft, never()).setTraceAllowedAssociations(anyInt());
            verify(draft, never()).setFeedbackAllowedIterations(anyInt());
            verify(draft, never()).setEnableReflection(anyBoolean());
            verify(draft, never()).addLinks(anyList());
            verify(activityDraftRepository).save(draft);
          }

          @Test
          void thenItShouldOnlyUpdateProvidedFields() {
            BddLogger.then("only provided fields should be updated");

            activityService.updateActivityDraft(
                draftId, "Titre seul", null, null, null, null, null, null, null, null, null, null);

            verify(draft).setTitle("Titre seul");
            verify(draft, never()).setThematic(any());
            verify(draft, never()).setSummary(any());
            verify(draft, never()).setDescription(any());
            verify(draft, never()).setRecommendedCompletionContexts(any());
            verify(draft, never()).setStartDate(any());
            verify(draft, never()).setEndDate(any());
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
                activityService.updateActivityDraft(
                    draftId, "Titre", null, null, null, null, null, null, null, null, null, null);

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
                    activityService.updateActivityDraft(
                        draftId, "Titre", null, null, null, null, null, null, null, null, null,
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
                  activityService.updateActivityDraft(
                      draftId, "Titre", null, null, null, null, null, null, null, null, null,
                      null));

          verify(activityDraftRepository, never()).save(any());
        }
      }
    }

    @Nested
    class WhenAddingADraftFile {

      UUID draftId;
      Staff loggedInStaff;
      ActivityDraft draft;
      byte[] content;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("adding a file to an activity draft");
        draftId = UUID.randomUUID();
        loggedInStaff = mock(Staff.class);
        draft = mock(ActivityDraft.class);
        content = new byte[] {1, 2, 3};
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
          }

          @Test
          void thenItShouldUploadAndAddFileWhenTypeIsAllowed() {
            BddLogger.then("the file should be uploaded and added to the draft");

            File uploaded = mock(File.class);
            when(fileResourceService.upload("report.pdf", "application/pdf", 3L, content, true))
                .thenReturn(uploaded);

            File result =
                activityService.addDraftFile(draftId, "report.pdf", "application/pdf", 3L, content);

            assertEquals(uploaded, result);
            verify(draft).addFile(uploaded);
            verify(activityDraftRepository).save(draft);
          }

          @Test
          void thenItShouldThrowFileTypeNotSupportedExceptionWhenTypeIsNotAllowed() {
            BddLogger.then("the service should throw FileTypeNotSupportedException");

            assertThrows(
                FileTypeNotSupportedException.class,
                () ->
                    activityService.addDraftFile(
                        draftId, "video.mp4", EFileType.MP4.getMimeType(), 3L, content));

            verifyNoInteractions(fileResourceService);
            verify(activityDraftRepository, never()).save(any());
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
          void thenItShouldThrowActivityDraftNotFoundException() {
            BddLogger.then("the service should throw ActivityDraftNotFoundException");

            assertThrows(
                ActivityDraftNotFoundException.class,
                () ->
                    activityService.addDraftFile(
                        draftId, "report.pdf", "application/pdf", 3L, content));

            verifyNoInteractions(fileResourceService);
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
                  activityService.addDraftFile(
                      draftId, "report.pdf", "application/pdf", 3L, content));

          verifyNoInteractions(fileResourceService);
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

            when(draft.getRecommendedCompletionContexts()).thenReturn(Optional.empty());

            when(draft.getStartDate()).thenReturn(Optional.empty());
            when(draft.getEndDate()).thenReturn(Optional.empty());

            Activity result = activityService.publish(draftId);

            assertNotNull(result);
            assertTrue(result.getRecommendedCompletionContexts().isEmpty());
            assertTrue(result.getStartDate().isEmpty());
            assertTrue(result.getEndDate().isEmpty());
          }

          @Test
          void thenItShouldPreserveAllDraftFields() {
            BddLogger.then("all draft fields should be preserved");

            List<String> links = List.of("https://example.com", "https://avenirs-esr.fr");
            when(draft.getTitle()).thenReturn("Titre complet");
            when(draft.getThematic()).thenReturn(EActivityThematic.SELF_KNOWLEDGE);
            when(draft.getSummary()).thenReturn(Optional.of("Résumé complet"));
            when(draft.getDescription()).thenReturn(Optional.of("<p>Description complète</p>"));
            when(draft.getRecommendedCompletionContexts())
                .thenReturn(Optional.of("Avant entretien"));
            when(draft.getStartDate()).thenReturn(Optional.of(LocalDate.parse("2026-06-01")));
            when(draft.getEndDate()).thenReturn(Optional.of(LocalDate.parse("2030-06-30")));
            when(draft.isEnableReflection()).thenReturn(false);
            when(draft.getTraceAllowedAssociations()).thenReturn(3);
            when(draft.getFeedbackAllowedIterations()).thenReturn(5);
            when(draft.getLinks()).thenReturn(links);

            Activity result = activityService.publish(draftId);

            assertEquals(draftId, result.getId());
            assertEquals("Titre complet", result.getTitle());
            assertEquals(EActivityThematic.SELF_KNOWLEDGE, result.getThematic());
            assertEquals("Résumé complet", result.getSummary());
            assertEquals("<p>Description complète</p>", result.getDescription());
            assertEquals("Avant entretien", result.getRecommendedCompletionContexts().get());
            assertEquals(LocalDate.parse("2026-06-01"), result.getStartDate().get());
            assertEquals(LocalDate.parse("2030-06-30"), result.getEndDate().get());
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

          @Test
          void thenItShouldThrowFieldValidationExceptionWhenDescriptionIsEmpty() {
            BddLogger.then("the service should throw FieldValidationException");

            when(draft.getDescription()).thenReturn(Optional.empty());

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
    class WhenPublishingADraftThatSharesIdWithAnExistingActivity {

      UUID activityId;
      Staff staff;
      ActivityDraft draft;
      Activity existingActivity;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("publishing a draft that shares its id with an existing activity");

        activityId = UUID.randomUUID();
        staff = mock(Staff.class);
        draft = mock(ActivityDraft.class);
        existingActivity = mock(Activity.class);

        when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
        when(activityDraftRepository.findById(activityId)).thenReturn(Optional.of(draft));
        when(draft.getAuthor()).thenReturn(staff);
        when(draft.getSummary()).thenReturn(Optional.of("Nouveau résumé"));
        when(draft.getTitle()).thenReturn("Nouveau titre");
        when(draft.getThematic()).thenReturn(EActivityThematic.EXPERIENCES);
        when(draft.getDescription()).thenReturn(Optional.of("Nouvelle description"));
        when(draft.getRecommendedCompletionContexts()).thenReturn(Optional.of("Nouvelle période"));
        when(draft.getStartDate()).thenReturn(Optional.of(LocalDate.parse("2026-06-01")));
        when(draft.getEndDate()).thenReturn(Optional.of(LocalDate.parse("2030-06-30")));
        when(draft.getBanner()).thenReturn(Optional.empty());
        when(draft.getLinks()).thenReturn(List.of("https://example.com"));
        when(draft.isEnableReflection()).thenReturn(false);
        when(draft.getTraceAllowedAssociations()).thenReturn(3);
        when(draft.getFeedbackAllowedIterations()).thenReturn(5);

        when(activityRepository.findById(activityId)).thenReturn(Optional.of(existingActivity));
        when(activityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(existingActivity.getId()).thenReturn(activityId);
      }

      @Nested
      class AndTheActivityHasNoEnrolledStudents {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity has no enrolled students");
          when(declaredActivityService.getEnrolledStudents(existingActivity)).thenReturn(List.of());
        }

        @Test
        void thenItShouldNotSendAnyNotification() {
          BddLogger.then("no notification should be sent since there is no student to notify");

          activityService.publish(activityId);

          verifyNoInteractions(notificationService);
        }

        @Test
        void thenItShouldUpdateExistingActivityInsteadOfCreatingANewOne() {
          BddLogger.then("the existing activity should be reused and updated, not recreated");

          Activity result = activityService.publish(activityId);

          assertSame(existingActivity, result);
          verify(existingActivity).setTitle("Nouveau titre");
          verify(existingActivity).setThematic(EActivityThematic.EXPERIENCES);
          verify(existingActivity).setDescription("Nouvelle description");
          verify(existingActivity).setSummary("Nouveau résumé");
          verify(existingActivity).setRecommendedCompletionContexts("Nouvelle période");
          verify(existingActivity).setStartDate(LocalDate.parse("2026-06-01"));
          verify(existingActivity).setEndDate(LocalDate.parse("2030-06-30"));
          verify(existingActivity, never()).setBanner(any());
          verify(existingActivity).setLinks(List.of("https://example.com"));
          verify(existingActivity).setStatus(EActivityStatus.PUBLISHED);
          verify(activityRepository).save(existingActivity);
          verify(activityDraftRepository).removeFromDatabase(draft);
        }

        @Test
        void thenItShouldUpdateNotEditableFieldsOfExistingActivity() {
          BddLogger.then(
              "enableReflection, traceAllowedAssociations and feedbackAllowedIterations should be"
                  + " updated when no student is enrolled");

          activityService.publish(activityId);

          verify(existingActivity).setEnableReflection(false);
          verify(existingActivity).setTraceAllowedAssociations(3);
          verify(existingActivity).setFeedbackAllowedIterations(5);
        }

        @Test
        void thenItShouldRepublishAnUnpublishedActivity() {
          BddLogger.then("the existing activity should be republished through its draft");

          activityService.publish(activityId);

          verify(existingActivity).setStatus(EActivityStatus.PUBLISHED);
        }
      }

      @Nested
      class AndTheActivityHasEnrolledStudents {

        Student student1;
        Student student2;
        User user1;
        User user2;
        DeclaredActivity declaredActivity1;
        DeclaredActivity declaredActivity2;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity has enrolled students");
          student1 = mock(Student.class);
          student2 = mock(Student.class);
          user1 = mock(User.class);
          user2 = mock(User.class);
          declaredActivity1 = mock(DeclaredActivity.class);
          declaredActivity2 = mock(DeclaredActivity.class);
          lenient().when(student1.getUser()).thenReturn(user1);
          lenient().when(student2.getUser()).thenReturn(user2);
          lenient().when(declaredActivity1.getStudent()).thenReturn(student1);
          lenient().when(declaredActivity2.getStudent()).thenReturn(student2);
          lenient().when(declaredActivity1.getActivity()).thenReturn(existingActivity);
          lenient().when(declaredActivity2.getActivity()).thenReturn(existingActivity);
          when(declaredActivityService.getEnrolledStudents(existingActivity))
              .thenReturn(List.of(declaredActivity1, declaredActivity2));
        }

        @Test
        void thenItShouldUpdateEditableFieldsOfExistingActivity() {
          BddLogger.then("only the fields editable while students are enrolled should be updated");

          Activity result = activityService.publish(activityId);

          assertSame(existingActivity, result);
          verify(existingActivity).setTitle("Nouveau titre");
          verify(existingActivity).setThematic(EActivityThematic.EXPERIENCES);
          verify(existingActivity).setDescription("Nouvelle description");
          verify(existingActivity).setSummary("Nouveau résumé");
          verify(existingActivity).setRecommendedCompletionContexts("Nouvelle période");
          verify(existingActivity).setStartDate(LocalDate.parse("2026-06-01"));
          verify(existingActivity).setEndDate(LocalDate.parse("2030-06-30"));
          verify(existingActivity, never()).setBanner(any());
          verify(existingActivity).setLinks(List.of("https://example.com"));
          verify(existingActivity).setStatus(EActivityStatus.PUBLISHED);
          verify(activityRepository).save(existingActivity);
          verify(activityDraftRepository).removeFromDatabase(draft);
        }

        @Test
        void thenItShouldNotOverwriteNotEditableFieldsOfExistingActivity() {
          BddLogger.then(
              "enableReflection, traceAllowedAssociations and feedbackAllowedIterations should not"
                  + " be touched when students are enrolled");

          activityService.publish(activityId);

          verify(existingActivity, never()).setEnableReflection(anyBoolean());
          verify(existingActivity, never()).setTraceAllowedAssociations(anyInt());
          verify(existingActivity, never()).setFeedbackAllowedIterations(anyInt());
        }

        @Test
        @SuppressWarnings("unchecked")
        void thenItShouldNotifyEachEnrolledStudentAboutTheUpdatedFields() {
          BddLogger.then("each enrolled student should be notified about the updated fields");

          activityService.publish(activityId);

          ArgumentCaptor<List<ActivityUpdatedNotification>> captor =
              ArgumentCaptor.forClass(List.class);
          verify(notificationService).notifyAll(captor.capture());
          List<ActivityUpdatedNotification> notifications = captor.getValue();

          assertEquals(2, notifications.size());
          assertEquals(user1, notifications.get(0).build().getUser());
          assertEquals(user2, notifications.get(1).build().getUser());

          List<EActivityUpdatableField> expectedUpdatedFields =
              List.of(
                  EActivityUpdatableField.ACTIVITY_TITLE,
                  EActivityUpdatableField.SUMMARY,
                  EActivityUpdatableField.DESCRIPTION,
                  EActivityUpdatableField.RECOMMENDED_COMPLETION_CONTEXTS,
                  EActivityUpdatableField.THEMATIC,
                  EActivityUpdatableField.FILES_AND_LINKS);
          ActivityModifiedParameters parameters =
              (ActivityModifiedParameters) notifications.getFirst().build().getParameters();
          assertEquals(expectedUpdatedFields, parameters.updatedFields());
        }

        @Nested
        class AndNoTrackedFieldActuallyChanged {

          @BeforeEach
          void setupAnd() {
            BddLogger.and("none of the notifiable fields differ from the draft");
            when(existingActivity.getTitle()).thenReturn("Nouveau titre");
            when(existingActivity.getSummary()).thenReturn("Nouveau résumé");
            when(existingActivity.getDescription()).thenReturn("Nouvelle description");
            when(existingActivity.getRecommendedCompletionContexts())
                .thenReturn(Optional.of("Nouvelle période"));
            when(existingActivity.getThematic()).thenReturn(EActivityThematic.EXPERIENCES);
            when(existingActivity.getBanner()).thenReturn(Optional.empty());
            when(existingActivity.getLinks()).thenReturn(List.of("https://example.com"));
          }

          @Test
          void thenItShouldNotSendAnyNotification() {
            BddLogger.then("no notification should be sent since nothing actually changed");

            activityService.publish(activityId);

            verifyNoInteractions(notificationService);
          }
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
                null,
                true,
                -1,
                -1,
                null,
                List.of(),
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
            null,
            true,
            -1,
            -1,
            null,
            List.of(),
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
              .thenReturn("<h3>Objectives</h3><p>Test activity description</p>");
          when(activity.getRecommendedCompletionContexts()).thenReturn(Optional.of("2026"));
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
          assertEquals("2026", result.recommendedCompletionContexts());
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

        when(staffActivityOverviewRepository.findAllByAuthorAndStatus(staff, null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(overviewData), pageInfo));

        var result = activityService.staffActivityWorkingSpace(pageCriteria, null);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(pageInfo, result.pageInfo());
        assertEquals(overviewData.activityId(), result.content().getFirst().activityId());
      }

      @Test
      void thenItShouldReturnEmptyWhenNoActivitiesExist() {
        BddLogger.then("an empty paged result should be returned");

        when(staffActivityOverviewRepository.findAllByAuthorAndStatus(staff, null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

        var result = activityService.staffActivityWorkingSpace(pageCriteria, null);

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

        when(staffActivityOverviewRepository.findAllByAuthorAndStatus(staff, null, pageCriteria))
            .thenReturn(
                new PagedResult<>(List.of(draft, published, unpublished), new PageInfo(0, 8, 3)));

        var result = activityService.staffActivityWorkingSpace(pageCriteria, null);

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
        when(staffActivityOverviewRepository.findAllByAuthorAndStatus(staff, null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

        activityService.staffActivityWorkingSpace(pageCriteria, null);

        verify(staffActivityOverviewRepository).findAllByAuthorAndStatus(staff, null, pageCriteria);
        verify(staffActivityOverviewRepository, never())
            .findAllByAuthorAndStatus(eq(otherStaff), eq(null), any());
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

        when(staffActivityOverviewRepository.findAllByAuthorAndStatus(staff, null, pageCriteria))
            .thenReturn(new PagedResult<>(List.of(overviewData), pageInfo));

        var result = activityService.staffActivityWorkingSpace(pageCriteria, null);
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

    @Nested
    class WhenDuplicatingAnActivity {

      UUID activityId;
      Staff loggedInStaff;
      Staff originalAuthor;

      @BeforeEach
      void setupWhen() {
        BddLogger.when("duplicating an activity");
        activityId = UUID.randomUUID();
        loggedInStaff = mock(Staff.class);
        originalAuthor = mock(Staff.class);
        when(loggedInUserService.getLoggedInStaff()).thenReturn(loggedInStaff);
      }

      @Nested
      class AndTheActivityExists {

        Activity activity;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity exists and belongs to another staff");
          activity = anActivity(null, List.of());
          when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));
          when(activityDraftRepository.save(any()))
              .thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void thenItShouldSaveANewDraftWithANewIdAndTheLoggedInStaffAsAuthor() {
          BddLogger.then("a brand new draft owned by the logged-in staff should be saved");

          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertNotEquals(activityId, result.getId());
          assertEquals(loggedInStaff, result.getAuthor());
          verify(activityDraftRepository).save(result);
        }

        @Test
        void thenItShouldCopyEveryActivityField() {
          BddLogger.then("the draft should hold the same content as the source activity");

          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertEquals("Activité à dupliquer", result.getTitle());
          assertEquals(EActivityThematic.EXPERIENCES, result.getThematic());
          assertEquals("Un résumé", result.getSummary().orElseThrow());
          assertEquals("<p>Une description</p>", result.getDescription().orElseThrow());
          assertEquals("2026", result.getRecommendedCompletionContexts().orElseThrow());
          assertEquals(LocalDate.parse("2026-06-01"), result.getStartDate().orElseThrow());
          assertEquals(LocalDate.parse("2026-06-30"), result.getEndDate().orElseThrow());
          assertEquals(3, result.getTraceAllowedAssociations());
          assertEquals(2, result.getFeedbackAllowedIterations());
          assertTrue(result.isEnableReflection());
          assertEquals(List.of("https://example.com"), result.getLinks());
        }

        @Test
        void thenItShouldResetCreationAndUpdateDates() {
          BddLogger.then("the draft dates should be refreshed instead of copied");

          Instant beforeDuplication = Instant.now();
          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertFalse(result.getCreatedAt().isBefore(beforeDuplication));
          assertFalse(result.getUpdatedAt().isBefore(beforeDuplication));
        }

        @Test
        void thenItShouldLeaveTheSourceActivityUntouched() {
          BddLogger.then("the source activity should stay published and never be saved");

          activityService.duplicateActivity(activityId);

          assertEquals(EActivityStatus.PUBLISHED, activity.getStatus());
          verify(activityRepository, never()).save(any());
          verifyNoInteractions(declaredActivityService);
        }

        @Test
        void thenItShouldNotRequireTheLoggedInStaffToBeTheAuthor() {
          BddLogger.then("any staff should be allowed to duplicate the activity");

          assertDoesNotThrow(() -> activityService.duplicateActivity(activityId));
        }
      }

      @Nested
      class AndTheActivityHasABannerAndFiles {

        File banner;
        File attachment;
        File copiedBanner;
        File copiedAttachment;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity has a banner and an attached file");
          banner = aFile();
          attachment = aFile();
          copiedBanner = mock(File.class);
          copiedAttachment = mock(File.class);

          when(activityRepository.findById(activityId))
              .thenReturn(Optional.of(anActivity(banner, List.of(attachment))));
          when(activityDraftRepository.save(any()))
              .thenAnswer(invocation -> invocation.getArgument(0));
          when(fileResourceService.copy(banner.getId())).thenReturn(copiedBanner);
          when(fileResourceService.copy(attachment.getId())).thenReturn(copiedAttachment);
        }

        @Test
        void thenItShouldCopyThemInsteadOfSharingTheSourceOnes() {
          BddLogger.then("the draft should reference copies instead of the source files");

          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertEquals(copiedBanner, result.getBanner().orElseThrow());
          assertEquals(List.of(copiedAttachment), result.getFiles());
          verify(fileResourceService).copy(banner.getId());
          verify(fileResourceService).copy(attachment.getId());
        }
      }

      @Nested
      class AndTheActivityDoesNotExist {

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the activity does not exist");
          when(activityRepository.findById(activityId)).thenReturn(Optional.empty());
        }

        @Test
        void thenItShouldThrowActivityNotFoundException() {
          BddLogger.then("the service should throw ActivityNotFoundException");

          assertThrows(
              ActivityNotFoundException.class, () -> activityService.duplicateActivity(activityId));

          verify(activityDraftRepository, never()).save(any());
          verifyNoInteractions(fileResourceService);
        }
      }

      @Nested
      class AndTheSourceIsAnUnpublishedDraft {

        ActivityDraft sourceDraft;

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the source is an unpublished draft, not a published activity");
          when(activityRepository.findById(activityId)).thenReturn(Optional.empty());
          sourceDraft = aDraft(null, List.of());
          when(activityDraftRepository.findById(activityId)).thenReturn(Optional.of(sourceDraft));
          when(activityDraftRepository.save(any()))
              .thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Test
        void thenItShouldSaveANewDraftWithANewIdAndTheLoggedInStaffAsAuthor() {
          BddLogger.then("a brand new draft owned by the logged-in staff should be saved");

          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertNotEquals(activityId, result.getId());
          assertEquals(loggedInStaff, result.getAuthor());
          verify(activityDraftRepository).save(result);
        }

        @Test
        void thenItShouldCopyEveryDraftField() {
          BddLogger.then("the new draft should hold the same content as the source draft");

          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertEquals("Brouillon à dupliquer", result.getTitle());
          assertEquals(EActivityThematic.EXPERIENCES, result.getThematic());
          assertEquals("Un résumé", result.getSummary().orElseThrow());
          assertEquals("<p>Une description</p>", result.getDescription().orElseThrow());
          assertEquals("2026", result.getRecommendedCompletionContexts().orElseThrow());
          assertEquals(LocalDate.parse("2026-06-01"), result.getStartDate().orElseThrow());
          assertEquals(LocalDate.parse("2026-06-30"), result.getEndDate().orElseThrow());
          assertEquals(3, result.getTraceAllowedAssociations());
          assertEquals(2, result.getFeedbackAllowedIterations());
          assertTrue(result.isEnableReflection());
          assertEquals(List.of("https://example.com"), result.getLinks());
        }

        @Test
        void thenItShouldResetCreationAndUpdateDates() {
          BddLogger.then("the draft dates should be refreshed instead of copied");

          Instant beforeDuplication = Instant.now();
          ActivityDraft result = activityService.duplicateActivity(activityId);

          assertFalse(result.getCreatedAt().isBefore(beforeDuplication));
          assertFalse(result.getUpdatedAt().isBefore(beforeDuplication));
        }

        @Test
        void thenItShouldLeaveTheSourceDraftUntouched() {
          BddLogger.then("the source draft should never be modified or saved again");

          activityService.duplicateActivity(activityId);

          verify(activityDraftRepository, never()).save(sourceDraft);
          verify(activityDraftRepository, never()).removeFromDatabase(any());
        }

        @Test
        void thenItShouldNotRequireTheLoggedInStaffToBeTheAuthor() {
          BddLogger.then("any staff should be allowed to duplicate the draft");

          assertDoesNotThrow(() -> activityService.duplicateActivity(activityId));
        }

        @Nested
        class AndTheDraftHasABannerAndFiles {

          File banner;
          File attachment;
          File copiedBanner;
          File copiedAttachment;

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the draft has a banner and an attached file");
            banner = aFile();
            attachment = aFile();
            copiedBanner = mock(File.class);
            copiedAttachment = mock(File.class);

            when(activityDraftRepository.findById(activityId))
                .thenReturn(Optional.of(aDraft(banner, List.of(attachment))));
            when(fileResourceService.copy(banner.getId())).thenReturn(copiedBanner);
            when(fileResourceService.copy(attachment.getId())).thenReturn(copiedAttachment);
          }

          @Test
          void thenItShouldCopyThemInsteadOfSharingTheSourceOnes() {
            BddLogger.then("the new draft should reference copies instead of the source files");

            ActivityDraft result = activityService.duplicateActivity(activityId);

            assertEquals(copiedBanner, result.getBanner().orElseThrow());
            assertEquals(List.of(copiedAttachment), result.getFiles());
            verify(fileResourceService).copy(banner.getId());
            verify(fileResourceService).copy(attachment.getId());
          }
        }

        private ActivityDraft aDraft(File banner, List<File> files) {
          ActivityDraft d =
              ActivityDraft.toDomain(
                  activityId,
                  Instant.parse("2020-01-01T00:00:00Z"),
                  Instant.parse("2021-01-01T00:00:00Z"),
                  "Brouillon à dupliquer",
                  originalAuthor,
                  EActivityThematic.EXPERIENCES,
                  "Un résumé",
                  "<p>Une description</p>",
                  "2026",
                  LocalDate.parse("2026-06-01"),
                  LocalDate.parse("2026-06-30"),
                  3,
                  2,
                  true,
                  banner,
                  List.of("https://example.com"),
                  files);
          return d;
        }
      }

      private Activity anActivity(File banner, List<File> files) {
        return Activity.toDomain(
            activityId,
            originalAuthor,
            "Activité à dupliquer",
            EActivityThematic.EXPERIENCES,
            "Un résumé",
            EActivityStatus.PUBLISHED,
            "<p>Une description</p>",
            "2026",
            LocalDate.parse("2026-06-01"),
            LocalDate.parse("2026-06-30"),
            true,
            3,
            2,
            banner,
            List.of("https://example.com"),
            files,
            Instant.parse("2020-01-01T00:00:00Z"),
            Instant.parse("2021-01-01T00:00:00Z"));
      }

      private File aFile() {
        File file = mock(File.class);
        when(file.getId()).thenReturn(UUID.randomUUID());
        return file;
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
    when(draft.getRecommendedCompletionContexts()).thenReturn(Optional.of("2026"));
    when(draft.getStartDate()).thenReturn(Optional.empty());
    when(draft.getEndDate()).thenReturn(Optional.empty());
    when(draft.isEnableReflection()).thenReturn(true);
    when(draft.getTraceAllowedAssociations()).thenReturn(-1);
    when(draft.getFeedbackAllowedIterations()).thenReturn(-1);
    when(draft.getBanner()).thenReturn(Optional.empty());
    when(draft.getLinks()).thenReturn(links);
  }
}
