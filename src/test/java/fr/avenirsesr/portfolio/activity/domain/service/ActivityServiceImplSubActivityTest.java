package fr.avenirsesr.portfolio.activity.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.model.FileDownload;
import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.staff.activity.domain.data.SubActivityOverviewData;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.SubActivityFieldNotAllowedException;
import fr.avenirsesr.portfolio.staff.activity.domain.exception.SubActivityOperationNotAllowedException;
import fr.avenirsesr.portfolio.staff.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.domain.port.input.ActivityAuthorizationService;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityDraftRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.ActivityViewRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.port.output.repository.StaffActivityOverviewRepository;
import fr.avenirsesr.portfolio.staff.activity.domain.service.ActivityServiceImpl;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStaffException;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.port.output.client.AccessClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StaffFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ActivityServiceImplSubActivityTest {

  @Mock private ActivityRepository activityRepository;
  @Mock private ActivityDraftRepository activityDraftRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private DeclaredActivityService declaredActivityService;
  @Mock private StaffActivityOverviewRepository staffActivityOverviewRepository;
  @Mock private NotificationService notificationService;
  @Mock private FileResourceService fileResourceService;
  @Mock private ActivityViewRepository activityViewRepository;
  @Mock private AccessClient accessClient;
  @Mock private UserPrincipalRepository userPrincipalRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private ActivityAuthorizationService activityAuthorizationService;

  @InjectMocks private ActivityServiceImpl activityService;

  private Staff staff;
  private Activity mainActivity;
  private Activity firstSubActivity;
  private Activity secondSubActivity;
  private ActivityDraft mainDraft;
  private ActivityDraft firstSubActivityDraft;
  private ActivityDraft secondSubActivityDraft;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    staff = StaffFixture.create().toModel();
    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);

    mainActivity = ActivityFixture.create().withAuthor(staff).toModel();
    firstSubActivity = publishedSubActivity(mainActivity.getId(), "First step", List.of());
    secondSubActivity = publishedSubActivity(firstSubActivity.getId(), "Second step", List.of());

    mainDraft = ActivityDraft.create("Main draft", staff);
    firstSubActivityDraft = subActivityDraft(mainDraft.getId(), "First draft step");
    secondSubActivityDraft = subActivityDraft(firstSubActivityDraft.getId(), "Second draft step");
  }

  private Activity publishedSubActivity(UUID previousActivityId, String title, List<File> files) {
    return Activity.createSubActivity(
        UUID.randomUUID(),
        staff,
        mainActivity.getId(),
        previousActivityId,
        title,
        "<p>Consigne</p>",
        null,
        null,
        null,
        List.of(),
        files);
  }

  private ActivityDraft subActivityDraft(UUID previousActivityId, String title) {
    var draft = ActivityDraft.create(title, staff);
    draft.attachAsSubActivity(mainDraft.getId(), previousActivityId);
    return draft;
  }

  private void givenPublished(Activity... activities) {
    for (var activity : activities) {
      when(activityRepository.findById(activity.getId())).thenReturn(Optional.of(activity));
    }
  }

  private void givenDrafts(ActivityDraft... drafts) {
    for (var draft : drafts) {
      when(activityDraftRepository.findById(draft.getId())).thenReturn(Optional.of(draft));
    }
  }

  @Nested
  class WhenCallingAnOperationReservedToMainActivities {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("a published sub-activity and a sub-activity draft");
      givenPublished(mainActivity, firstSubActivity);
      givenDrafts(mainDraft, firstSubActivityDraft);
    }

    @Test
    void thenPublishingASubActivityDraftShouldBeRejected() {
      BddLogger.then("publishing the sub-activity draft should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.publish(firstSubActivityDraft.getId()));
      verify(activityRepository, never()).save(any());
    }

    @Test
    void thenUnpublishingASubActivityShouldBeRejected() {
      BddLogger.then("unpublishing the sub-activity should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.unpublish(firstSubActivity.getId()));
      verify(activityRepository, never()).save(any());
    }

    @Test
    void thenCreatingADraftFromASubActivityShouldBeRejected() {
      BddLogger.then("creating a draft from the sub-activity should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.createDraftFromActivity(firstSubActivity.getId()));
      verify(activityDraftRepository, never()).save(any());
    }

    @Test
    void thenDuplicatingAPublishedSubActivityShouldBeRejected() {
      BddLogger.then("duplicating the published sub-activity should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.duplicateActivity(firstSubActivity.getId(), "Copy"));
      verify(activityDraftRepository, never()).save(any());
    }

    @Test
    void thenDuplicatingASubActivityDraftShouldBeRejected() {
      BddLogger.then("duplicating the sub-activity draft should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.duplicateActivity(firstSubActivityDraft.getId(), "Copy"));
      verify(activityDraftRepository, never()).save(any());
    }

    @Test
    void thenDeletingASubActivityDraftShouldBeRejected() {
      BddLogger.then("deleting the sub-activity draft should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.deleteDraft(firstSubActivityDraft.getId()));
      verify(activityDraftRepository, never()).removeFromDatabase(any());
    }

    @Test
    void thenDeletingASubActivityDefinitivelyShouldBeRejected() {
      BddLogger.then("deleting the sub-activity definitively should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.deleteActivityDefinitively(firstSubActivity.getId()));
      verify(activityRepository, never()).removeFromDatabase(any());
      verify(declaredActivityService, never()).deleteAllOfActivity(any());
    }

    @Test
    void thenGettingTheDashboardOfASubActivityShouldBeRejected() {
      BddLogger.then("the dashboard and the inactive students should not be available");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.getActivityDashboard(firstSubActivity.getId()));
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.getInactiveStudents(firstSubActivity.getId()));
    }

    @Test
    void thenManagingTheBannerOfASubActivityDraftShouldBeRejected() {
      BddLogger.then("uploading or deleting a banner on the sub-activity draft should be rejected");
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () ->
              activityService.uploadDraftBanner(
                  firstSubActivityDraft.getId(), "banner.png", "image/png", 3, new byte[3]));
      assertThrows(
          SubActivityOperationNotAllowedException.class,
          () -> activityService.deleteDraftBanner(firstSubActivityDraft.getId()));
      verify(fileResourceService, never()).upload(any(), any(), anyLong(), any(), anyBoolean());
    }
  }

  @Nested
  class WhenUpdatingASubActivityDraft {

    @BeforeEach
    void setupWhen() {
      BddLogger.given("a sub-activity draft");
      givenDrafts(mainDraft, firstSubActivityDraft);
      when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void update(Consumer<UpdateRequest> customizer) {
      var request = new UpdateRequest();
      customizer.accept(request);
      activityService.updateActivityDraft(
          firstSubActivityDraft.getId(),
          request.title,
          request.thematic,
          request.summary,
          request.description,
          request.recommendedCompletionContexts,
          request.startDate,
          request.endDate,
          request.traceAllowedAssociations,
          request.feedbackAllowedIterations,
          request.enableReflection,
          request.links,
          request.enableCompletionPeriod,
          request.targetInstitutionIds,
          request.targetGroupIds,
          request.national,
          request.assessment);
    }

    @Test
    void thenItShouldCheckTheEditRightOnTheMainActivityDraft() {
      BddLogger.then("the edit right should be checked on the main activity draft");
      update(request -> request.title = "New title");
      verify(activityAuthorizationService)
          .checkDraftRight(staff, mainDraft.getId(), EActivityRight.EDIT);
    }

    @Test
    void thenItShouldUpdateTheFieldsOfASubActivity() {
      BddLogger.then("the title, instructions, contexts, period and links should be updated");
      update(
          request -> {
            request.title = "New title";
            request.description = "<p>New instructions</p>";
            request.recommendedCompletionContexts = "New contexts";
            request.enableCompletionPeriod = true;
            request.startDate = LocalDate.parse("2026-09-01");
            request.endDate = LocalDate.parse("2026-12-31");
            request.links = List.of("https://example.com/step");
          });

      assertEquals("New title", firstSubActivityDraft.getTitle());
      assertEquals("<p>New instructions</p>", firstSubActivityDraft.getDescription().orElseThrow());
      assertEquals(
          "New contexts", firstSubActivityDraft.getRecommendedCompletionContexts().orElseThrow());
      assertEquals(
          LocalDate.parse("2026-09-01"), firstSubActivityDraft.getStartDate().orElseThrow());
      assertEquals(List.of("https://example.com/step"), firstSubActivityDraft.getLinks());
      verify(activityDraftRepository).save(firstSubActivityDraft);
    }

    @Test
    void thenSettingTheThematicShouldBeRejected() {
      assertRejected(request -> request.thematic = EActivityThematic.OTHER, "thematic");
    }

    @Test
    void thenSettingTheSummaryShouldBeRejected() {
      assertRejected(request -> request.summary = "Summary", "summary");
    }

    @Test
    void thenSettingTheTraceAllowedAssociationsShouldBeRejected() {
      assertRejected(request -> request.traceAllowedAssociations = 3, "traceAllowedAssociations");
    }

    @Test
    void thenSettingTheFeedbackAllowedIterationsShouldBeRejected() {
      assertRejected(request -> request.feedbackAllowedIterations = 3, "feedbackAllowedIterations");
    }

    @Test
    void thenEnablingTheReflectionShouldBeRejected() {
      assertRejected(request -> request.enableReflection = true, "enableReflection");
    }

    @Test
    void thenSettingATargetingShouldBeRejected() {
      assertRejected(
          request -> request.targetInstitutionIds = List.of(UUID.randomUUID()),
          "targetInstitutionIds");
      assertRejected(request -> request.targetGroupIds = List.of(), "targetGroupIds");
      assertRejected(request -> request.national = false, "national");
    }

    @Test
    void thenSettingAnAssessmentShouldBeRejected() {
      assertRejected(
          request -> request.assessment = new ActivityAssessment(true, null, null), "assessment");
    }

    private void assertRejected(Consumer<UpdateRequest> customizer, String fieldName) {
      BddLogger.then("setting " + fieldName + " on a sub-activity should be rejected");
      var exception =
          assertThrows(SubActivityFieldNotAllowedException.class, () -> update(customizer));
      assertThat(exception.getMessage()).contains(fieldName);
      verify(activityDraftRepository, never()).save(any());
    }
  }

  @Nested
  class WhenReadingASubActivity {

    @Test
    void thenTheEnrolledStudentsOfASubActivityDraftShouldBeThoseOfTheMainActivity() {
      BddLogger.given("a sub-activity draft whose main activity has enrolled students");
      var publishedSubActivity = mock(Activity.class);
      when(activityRepository.findById(mainDraft.getId())).thenReturn(Optional.of(mainActivity));
      when(activityRepository.findById(firstSubActivityDraft.getId()))
          .thenReturn(Optional.of(publishedSubActivity));
      when(declaredActivityService.countEnrolledStudents(mainActivity)).thenReturn(2);

      BddLogger.then("the sub-activity draft should report the enrolled students of the main");
      assertTrue(activityService.hasEnrolledStudents(firstSubActivityDraft));
      verify(declaredActivityService, never()).countEnrolledStudents(publishedSubActivity);
    }

    @Test
    void thenTheMainActivityOfASubActivityShouldBeResolved() {
      BddLogger.given("a published sub-activity");
      givenPublished(mainActivity);

      BddLogger.then("its main activity should be resolved, a main activity resolves to itself");
      assertSame(mainActivity, activityService.getMainActivityOf(firstSubActivity));
      assertSame(mainActivity, activityService.getMainActivityOf(mainActivity));
    }

    @Test
    void thenTheSubActivityOverviewsShouldBeListedInTheirOrder() {
      BddLogger.given("a main activity with two sub-activities");
      when(activityRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(firstSubActivity, secondSubActivity));
      when(activityDraftRepository.findAllSubActivitiesOf(mainDraft.getId()))
          .thenReturn(List.of(firstSubActivityDraft, secondSubActivityDraft));

      BddLogger.then("the overviews should expose the id and the title of each sub-activity");
      assertEquals(
          List.of(
              new SubActivityOverviewData(firstSubActivity.getId(), "First step"),
              new SubActivityOverviewData(secondSubActivity.getId(), "Second step")),
          activityService.getSubActivityOverviews(mainActivity.getId()));
      assertEquals(
          List.of(
              new SubActivityOverviewData(firstSubActivityDraft.getId(), "First draft step"),
              new SubActivityOverviewData(secondSubActivityDraft.getId(), "Second draft step")),
          activityService.getDraftSubActivityOverviews(mainDraft.getId()));
    }

    @Test
    void thenThePresentationOfASubActivityShouldRelyOnItsMainActivity() {
      BddLogger.given("a student enrolled in the main activity of a sub-activity");
      var student = mock(Student.class);
      var studentId = UUID.randomUUID();
      when(student.getId()).thenReturn(studentId);
      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      mainActivity.setStatus(EActivityStatus.UNPUBLISHED);
      givenPublished(mainActivity, firstSubActivity);
      var declaredActivity = mock(DeclaredActivity.class);
      var declaredActivityId = UUID.randomUUID();
      when(declaredActivity.getId()).thenReturn(declaredActivityId);
      when(declaredActivityService.getByActivity(mainActivity))
          .thenReturn(Optional.of(declaredActivity));
      when(declaredActivityService.getDeclaredActivityStatus(declaredActivity))
          .thenReturn(EDeclaredActivityStatus.IN_PROGRESS);

      BddLogger.when("getting the presentation of the sub-activity");
      var presentation =
          activityService.getActivityPresentation(
              EActivityStatus.PUBLISHED, firstSubActivity.getId());

      BddLogger.then("the status and the declared activity should be those of the main activity");
      assertEquals(EActivityStatus.UNPUBLISHED, presentation.activityStatus());
      assertEquals(declaredActivityId, presentation.subscribedDeclaredActivity().orElseThrow());
      assertEquals(mainActivity.getId(), presentation.mainActivityId().orElseThrow());
      assertTrue(presentation.subActivities().isEmpty());
      verify(activityViewRepository).recordView(firstSubActivity.getId(), studentId);
    }

    @Test
    void thenThePresentationOfAMainActivityShouldListItsSubActivities() {
      BddLogger.given("a main activity with two sub-activities");
      var student = mock(Student.class);
      when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
      givenPublished(mainActivity);
      when(activityRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(firstSubActivity, secondSubActivity));

      BddLogger.when("getting the presentation of the main activity");
      var presentation =
          activityService.getActivityPresentation(EActivityStatus.PUBLISHED, mainActivity.getId());

      BddLogger.then("the presentation should list the sub-activities in their order");
      assertTrue(presentation.mainActivityId().isEmpty());
      assertEquals(
          List.of(firstSubActivity.getId(), secondSubActivity.getId()),
          presentation.subActivities().stream().map(SubActivityOverviewData::id).toList());
    }

    @Test
    void thenAStudentEnrolledInTheMainActivityShouldDownloadTheFilesOfASubActivity() {
      BddLogger.given("a student enrolled in the main activity of a sub-activity holding a file");
      var file = mock(File.class);
      var fileId = UUID.randomUUID();
      when(file.getId()).thenReturn(fileId);
      var subActivityWithFile =
          publishedSubActivity(mainActivity.getId(), "Step with a file", List.of(file));
      givenPublished(mainActivity, subActivityWithFile);
      when(loggedInUserService.getLoggedInStaff()).thenThrow(new UserIsNotStaffException());
      var user = mock(User.class);
      var userId = UUID.randomUUID();
      when(user.getId()).thenReturn(userId);
      when(loggedInUserService.getLoggedInUser()).thenReturn(user);
      var student = mock(Student.class);
      when(studentRepository.findById(userId)).thenReturn(Optional.of(student));
      when(declaredActivityService.isEnrolled(mainActivity, student)).thenReturn(true);
      var download = mock(FileDownload.class);
      when(fileResourceService.download(fileId)).thenReturn(download);

      BddLogger.then("the file of the sub-activity should be downloadable");
      assertSame(
          download, activityService.downloadActivityFile(subActivityWithFile.getId(), fileId));
    }

    @Test
    void thenTheNavigationShouldIgnoreTheSubActivities() {
      BddLogger.given("a main activity and its sub-activities");
      when(activityRepository.findAll())
          .thenReturn(List.of(mainActivity, firstSubActivity, secondSubActivity));

      BddLogger.then("only the main activity should be listed in the navigation");
      var navigation = activityService.getActivityNavigation();
      assertEquals(
          List.of(mainActivity), navigation.values().stream().flatMap(List::stream).toList());
    }
  }

  @Nested
  class WhenCascadingOnTheSubActivitiesOfAMainActivity {

    @Test
    void thenCreatingADraftShouldCopyTheSubActivitiesAndRemoveThemWithoutEnrolledStudents() {
      BddLogger.given("a published main activity with two sub-activities and no enrolled student");
      givenPublished(mainActivity);
      when(activityDraftRepository.findById(mainActivity.getId())).thenReturn(Optional.empty());
      when(activityRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(firstSubActivity, secondSubActivity));
      when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      BddLogger.when("creating a draft from the main activity");
      activityService.createDraftFromActivity(mainActivity.getId());

      BddLogger.then("a draft sharing its id and its links should be saved for each sub-activity");
      @SuppressWarnings("unchecked")
      ArgumentCaptor<List<ActivityDraft>> savedDrafts = ArgumentCaptor.forClass(List.class);
      verify(activityDraftRepository).saveAll(savedDrafts.capture());
      var subActivityDrafts = savedDrafts.getValue();
      assertEquals(
          List.of(firstSubActivity.getId(), secondSubActivity.getId()),
          subActivityDrafts.stream().map(ActivityDraft::getId).toList());
      assertEquals(
          List.of(mainActivity.getId(), firstSubActivity.getId()),
          subActivityDrafts.stream()
              .map(draft -> draft.getPreviousActivityId().orElseThrow())
              .toList());
      assertTrue(
          subActivityDrafts.stream()
              .allMatch(
                  draft -> draft.getMainActivityId().orElseThrow().equals(mainActivity.getId())));

      BddLogger.and("the published sub-activities should be removed before the main activity");
      var removal = inOrder(activityRepository);
      removal
          .verify(activityRepository)
          .removeAllFromDatabase(List.of(secondSubActivity, firstSubActivity));
      removal.verify(activityRepository).removeFromDatabase(mainActivity);
    }

    @Test
    void thenCreatingADraftShouldKeepThePublishedSubActivitiesWithEnrolledStudents() {
      BddLogger.given("a published main activity with sub-activities and enrolled students");
      givenPublished(mainActivity);
      when(activityDraftRepository.findById(mainActivity.getId())).thenReturn(Optional.empty());
      when(activityRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(firstSubActivity, secondSubActivity));
      when(declaredActivityService.countEnrolledStudents(mainActivity)).thenReturn(1);
      when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      BddLogger.when("creating a draft from the main activity");
      activityService.createDraftFromActivity(mainActivity.getId());

      BddLogger.then("the published main activity and its sub-activities should be kept");
      verify(activityDraftRepository).saveAll(any());
      verify(activityRepository, never()).removeAllFromDatabase(any());
      verify(activityRepository, never()).removeFromDatabase(any());
    }

    @Test
    void thenDeletingTheMainDraftShouldDeleteTheSubActivityDraftsFirst() {
      BddLogger.given("a main draft with two sub-activity drafts");
      givenDrafts(mainDraft);
      when(activityDraftRepository.findAllSubActivitiesOf(mainDraft.getId()))
          .thenReturn(List.of(firstSubActivityDraft, secondSubActivityDraft));

      BddLogger.when("deleting the main draft");
      activityService.deleteDraft(mainDraft.getId());

      BddLogger.then("the sub-activity drafts should be removed from the last one, then the main");
      var removal = inOrder(activityDraftRepository);
      removal
          .verify(activityDraftRepository)
          .removeAllFromDatabase(List.of(secondSubActivityDraft, firstSubActivityDraft));
      removal.verify(activityDraftRepository).removeFromDatabase(mainDraft);
      verify(activityAuthorizationService).deleteAllOfDraft(mainDraft.getId());
    }

    @Test
    void thenDeletingTheMainActivityDefinitivelyShouldDeleteItsWholeStructure() {
      BddLogger.given("a main activity with published sub-activities and sub-activity drafts");
      var file = mock(File.class);
      var fileId = UUID.randomUUID();
      when(file.getId()).thenReturn(fileId);
      var subActivityWithFile =
          publishedSubActivity(mainActivity.getId(), "Step with a file", List.of(file));
      var addedSubActivityDraft = ActivityDraft.create("Added in the draft", staff);
      addedSubActivityDraft.attachAsSubActivity(mainActivity.getId(), subActivityWithFile.getId());
      givenPublished(mainActivity);
      when(activityDraftRepository.findById(mainActivity.getId())).thenReturn(Optional.empty());
      when(activityRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(subActivityWithFile));
      when(activityDraftRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(addedSubActivityDraft));

      BddLogger.when("deleting the main activity definitively");
      activityService.deleteActivityDefinitively(mainActivity.getId());

      BddLogger.then("the sub-activities, their drafts, their views and their files are deleted");
      verify(activityDraftRepository).removeAllFromDatabase(List.of(addedSubActivityDraft));
      verify(activityRepository).removeAllFromDatabase(List.of(subActivityWithFile));
      verify(activityRepository).removeFromDatabase(mainActivity);
      verify(activityViewRepository).deleteAllByActivityId(subActivityWithFile.getId());
      verify(activityViewRepository).deleteAllByActivityId(addedSubActivityDraft.getId());
      verify(fileResourceService).delete(fileId);
    }

    @Test
    void thenDuplicatingAPublishedMainActivityShouldDuplicateItsSubActivities() {
      BddLogger.given("a published main activity with two sub-activities, one holding a file");
      var file = mock(File.class);
      var fileId = UUID.randomUUID();
      when(file.getId()).thenReturn(fileId);
      var copiedFile = mock(File.class);
      when(fileResourceService.copy(fileId)).thenReturn(copiedFile);
      var subActivityWithFile =
          publishedSubActivity(firstSubActivity.getId(), "Step with a file", List.of(file));
      givenPublished(mainActivity);
      when(activityRepository.findAllSubActivitiesOf(mainActivity.getId()))
          .thenReturn(List.of(firstSubActivity, subActivityWithFile));
      when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      BddLogger.when("duplicating the main activity");
      var duplicate = activityService.duplicateActivity(mainActivity.getId(), "Copy");

      BddLogger.then("each sub-activity should be duplicated in order under the duplicate");
      @SuppressWarnings("unchecked")
      ArgumentCaptor<List<ActivityDraft>> savedDrafts = ArgumentCaptor.forClass(List.class);
      verify(activityDraftRepository).saveAll(savedDrafts.capture());
      var duplicatedSubActivities = savedDrafts.getValue();
      assertEquals(2, duplicatedSubActivities.size());
      var first = duplicatedSubActivities.get(0);
      var second = duplicatedSubActivities.get(1);
      assertEquals("First step", first.getTitle());
      assertEquals("Step with a file", second.getTitle());
      assertNotEquals(firstSubActivity.getId(), first.getId());
      assertNotEquals(subActivityWithFile.getId(), second.getId());
      assertEquals(duplicate.getId(), first.getMainActivityId().orElseThrow());
      assertEquals(duplicate.getId(), first.getPreviousActivityId().orElseThrow());
      assertEquals(duplicate.getId(), second.getMainActivityId().orElseThrow());
      assertEquals(first.getId(), second.getPreviousActivityId().orElseThrow());
      assertEquals(staff, second.getAuthor());
      assertEquals(List.of(copiedFile), second.getFiles());
      verify(activityAuthorizationService).grantAllDraftRights(duplicate.getId(), staff);
      verify(activityAuthorizationService, never()).grantAllDraftRights(first.getId(), staff);
    }

    @Test
    void thenDuplicatingAMainDraftShouldDuplicateItsSubActivityDrafts() {
      BddLogger.given("a main draft with two sub-activity drafts");
      when(activityRepository.findById(mainDraft.getId())).thenReturn(Optional.empty());
      givenDrafts(mainDraft);
      when(activityDraftRepository.findAllSubActivitiesOf(mainDraft.getId()))
          .thenReturn(List.of(firstSubActivityDraft, secondSubActivityDraft));
      when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

      BddLogger.when("duplicating the main draft");
      var duplicate = activityService.duplicateActivity(mainDraft.getId(), "Copy");

      BddLogger.then("each sub-activity draft should be duplicated under the duplicate");
      @SuppressWarnings("unchecked")
      ArgumentCaptor<List<ActivityDraft>> savedDrafts = ArgumentCaptor.forClass(List.class);
      verify(activityDraftRepository).saveAll(savedDrafts.capture());
      assertEquals(
          List.of("First draft step", "Second draft step"),
          savedDrafts.getValue().stream().map(ActivityDraft::getTitle).toList());
      assertTrue(
          savedDrafts.getValue().stream()
              .allMatch(
                  draft -> draft.getMainActivityId().orElseThrow().equals(duplicate.getId())));
    }

    @Test
    void thenAMainActivityWithoutSubActivitiesShouldNotTouchTheSubActivityStorage() {
      BddLogger.given("a main draft without sub-activity");
      givenDrafts(mainDraft);

      BddLogger.when("deleting the main draft");
      activityService.deleteDraft(mainDraft.getId());

      BddLogger.then("no sub-activity should be removed");
      verify(activityDraftRepository, never()).removeAllFromDatabase(any());
      verify(activityDraftRepository).removeFromDatabase(mainDraft);
    }
  }

  @Nested
  class WhenCreatingAPublishedSubActivity {

    @Test
    void thenItShouldBeAttachedToTheMainActivityOfItsPreviousActivity() {
      BddLogger.given("a published sub-activity used as previous activity");
      givenPublished(mainActivity, firstSubActivity);
      when(activityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
      var id = UUID.randomUUID();

      BddLogger.when("creating a sub-activity after it");
      var subActivity =
          activityService.createSubActivity(
              id,
              firstSubActivity.getId(),
              "Third step",
              "<p>Consigne</p>",
              null,
              null,
              null,
              List.of());

      BddLogger.then("it should be a neutral sub-activity of the main activity");
      assertEquals(id, subActivity.getId());
      assertEquals(mainActivity.getId(), subActivity.getMainActivityId().orElseThrow());
      assertEquals(firstSubActivity.getId(), subActivity.getPreviousActivityId().orElseThrow());
      assertEquals(staff, subActivity.getAuthor());
      assertFalse(subActivity.isEnableReflection());
      assertEquals(0, subActivity.getTraceAllowedAssociations());
      assertEquals(0, subActivity.getFeedbackAllowedIterations());
      assertTrue(subActivity.getAssessment().isEmpty());
      verify(activityAuthorizationService, never()).grantAllRights(any(), any());
    }
  }

  private static class UpdateRequest {
    String title;
    EActivityThematic thematic;
    String summary;
    String description;
    String recommendedCompletionContexts;
    LocalDate startDate;
    LocalDate endDate;
    Integer traceAllowedAssociations;
    Integer feedbackAllowedIterations;
    Boolean enableReflection;
    List<String> links;
    Boolean enableCompletionPeriod;
    List<UUID> targetInstitutionIds;
    List<UUID> targetGroupIds;
    Boolean national;
    ActivityAssessment assessment;
  }
}
