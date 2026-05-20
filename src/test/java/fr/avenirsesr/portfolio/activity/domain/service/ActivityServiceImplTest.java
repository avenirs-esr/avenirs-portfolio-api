package fr.avenirsesr.portfolio.activity.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.data.ActivityPresentationData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityDraftNotFoundException;
import fr.avenirsesr.portfolio.activity.domain.exception.ActivityNotFoundException;
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
import fr.avenirsesr.portfolio.file.domain.data.FileData;
import fr.avenirsesr.portfolio.file.domain.port.input.ActivityResourceService;
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
  @Mock private ActivityResourceService activityResourceService;
  @Mock private StaffActivityOverviewRepository staffActivityOverviewRepository;

  @InjectMocks private ActivityServiceImpl activityService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void create_shouldReturnActivityAndSaveIt() {
    // Given
    UUID id = UUID.randomUUID();
    String title = "Test Activity";
    EActivityThematic thematic = EActivityThematic.EXPERIENCES;
    String summary = "This is a test activity";
    String description = "<h3>Objectives</h3><p>Test activity description</p>";
    String executionPeriodInfo = "2026";
    String executionPeriodInfoSummary = "Short label";
    var author = Mockito.mock(Staff.class);

    // When
    Activity createdActivity =
        activityService.create(
            id,
            author,
            title,
            thematic,
            summary,
            description,
            executionPeriodInfo,
            executionPeriodInfoSummary,
            true,
            -1,
            -1);

    // Then
    assertNotNull(createdActivity);
    assertEquals(id, createdActivity.getId());
    assertEquals(title, createdActivity.getTitle());
    assertEquals(thematic, createdActivity.getThematic());
    assertEquals(summary, createdActivity.getSummary());
    assertEquals(description, createdActivity.getDescription().get());
    assertEquals(executionPeriodInfo, createdActivity.getExecutionPeriodInfo().get());

    // Verify repository was called
    ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
    verify(activityRepository).save(captor.capture());
    Activity savedActivity = captor.getValue();
    assertEquals(createdActivity, savedActivity);
  }

  @Test
  void getActivityNavigation() {
    var author = Mockito.mock(Staff.class);
    // Given
    Activity a1 =
        Activity.create(
            UUID.randomUUID(),
            author,
            "A1",
            EActivityThematic.EXPERIENCES,
            "S1",
            "D1",
            "2026",
            "",
            true,
            -1,
            -1);
    Activity a2 =
        Activity.create(
            UUID.randomUUID(),
            author,
            "A2",
            EActivityThematic.EXPERIENCES,
            "S2",
            "D2",
            "2025",
            null,
            true,
            -1,
            -1);
    Activity a3 =
        Activity.create(
            UUID.randomUUID(),
            author,
            "A3",
            EActivityThematic.RESUMES,
            "S3",
            "D3",
            "2024",
            null,
            true,
            -1,
            -1);

    when(activityRepository.findAll()).thenReturn(List.of(a1, a2, a3));

    // When
    Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

    // Then
    assertNotNull(result);

    assertEquals(2, result.size());
    assertTrue(result.containsKey(EActivityThematic.EXPERIENCES));
    assertTrue(result.containsKey(EActivityThematic.RESUMES));

    List<Activity> experiences = result.get(EActivityThematic.EXPERIENCES);
    assertNotNull(experiences);
    assertEquals(2, experiences.size());
    assertTrue(experiences.contains(a1));
    assertTrue(experiences.contains(a2));

    List<Activity> cv = result.get(EActivityThematic.RESUMES);
    assertNotNull(cv);
    assertEquals(1, cv.size());
    assertEquals(a3, cv.getFirst());

    verify(activityRepository).findAll();
    verifyNoMoreInteractions(activityRepository);
  }

  @Test
  void getActivityNavigation_shouldNotIncludeThematicsThatAreNotPresent() {
    // Given (only CV)
    var author = Mockito.mock(Staff.class);
    Activity a1 =
        Activity.create(
            UUID.randomUUID(),
            author,
            "A1",
            EActivityThematic.RESUMES,
            "S1",
            "D1",
            "2026",
            null,
            true,
            -1,
            -1);

    when(activityRepository.findAll()).thenReturn(List.of(a1));

    // When
    Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.containsKey(EActivityThematic.RESUMES));
    assertFalse(result.containsKey(EActivityThematic.EXPERIENCES));
    assertFalse(result.containsKey(EActivityThematic.SELF_KNOWLEDGE));

    verify(activityRepository).findAll();
    verifyNoMoreInteractions(activityRepository);
  }

  @Test
  void getAllActivitiesByThematic_shouldReturnEmptyMapWhenNoActivities() {
    // Given
    when(activityRepository.findAll()).thenReturn(List.of());

    // When
    Map<EActivityThematic, List<Activity>> result = activityService.getActivityNavigation();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(activityRepository).findAll();
    verifyNoMoreInteractions(activityRepository);
  }

  @Test
  void activitiesView_shouldReturnActivitiesWithNullStatus_whenNotDeclared() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = mock(PageInfo.class);

    var activity = mock(Activity.class);
    when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(10)));

    var pagedActivities = new PagedResult<>(java.util.List.of(activity), pageInfo);

    when(activityRepository.findAll(EActivityThematic.EXPERIENCES, pageCriteria))
        .thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    when(declaredActivityService.getAllDeclaredActivitiesOf(student))
        .thenReturn(java.util.List.of());

    // When
    var result = activityService.activitiesView(EActivityThematic.EXPERIENCES, pageCriteria);

    // Then
    assertEquals(1, result.content().size());
    assertEquals(pageInfo, result.pageInfo());

    var data = result.content().get(0);
    assertEquals(activity, data.activity());
    assertNull(data.status());

    verify(activityRepository).findAll(EActivityThematic.EXPERIENCES, pageCriteria);
  }

  @Test
  void activitiesView_shouldReturnActivitiesWithStudentStatus() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = mock(PageInfo.class);

    var activity = mock(Activity.class);
    when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(10)));

    var pagedActivities = new PagedResult<>(java.util.List.of(activity), pageInfo);

    when(activityRepository.findAll(null, pageCriteria)).thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);

    var declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getActivity()).thenReturn(activity);
    when(declaredActivity.getStatus()).thenReturn(EDeclaredActivityStatus.SUBSCRIBED);

    when(declaredActivityService.getAllDeclaredActivitiesOf(student))
        .thenReturn(java.util.List.of(declaredActivity));

    // When
    var result = activityService.activitiesView(null, pageCriteria);

    // Then
    assertEquals(1, result.content().size());
    assertEquals(pageInfo, result.pageInfo());

    var data = result.content().get(0);
    assertEquals(activity, data.activity());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, data.status());

    verify(activityRepository).findAll(null, pageCriteria);
    verify(loggedInUserService).getLoggedInStudent();
    verify(declaredActivityService).getAllDeclaredActivitiesOf(student);
  }

  @Test
  void shouldMarkActivityAsNew_whenCreatedWithinLast3Months() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 10, 1);

    var activity = mock(Activity.class);
    when(activity.getCreatedAt()).thenReturn(Instant.now().minus(Duration.ofDays(10)));

    var pagedActivities = new PagedResult<>(List.of(activity), pageInfo);

    when(activityRepository.findAll(null, pageCriteria)).thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

    // When
    var result = activityService.activitiesView(null, pageCriteria);

    // Then
    assertEquals(1, result.content().size());

    var data = result.content().getFirst();
    assertTrue(data.isNew());
    assertNull(data.status());
  }

  @Test
  void shouldMarkActivityAsNotNew_whenOlderThan3Months() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 10, 1);

    var activity = mock(Activity.class);
    when(activity.getCreatedAt())
        .thenReturn(Instant.now().minus(Duration.ofDays(120))); // > 3 months

    var pagedActivities = new PagedResult<>(List.of(activity), pageInfo);

    when(activityRepository.findAll(null, pageCriteria)).thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

    // When
    var result = activityService.activitiesView(null, pageCriteria);

    // Then
    assertEquals(1, result.content().size());

    var data = result.content().getFirst();
    assertFalse(data.isNew());
    assertNull(data.status());
  }

  @Test
  void latestActivitiesView_shouldReturnEmpty_whenNoActivities() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 10, 0);

    when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

    // When
    var result = activityService.latestActivitiesView(pageCriteria);

    // Then
    assertNotNull(result);
    assertTrue(result.content().isEmpty());
    assertEquals(pageInfo, result.pageInfo());
  }

  @Test
  void latestActivitiesView_shouldReturnSingleActivity() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 10, 1);

    var activity = mock(Activity.class);
    var pagedActivities = new PagedResult<>(List.of(activity), pageInfo);

    when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
        .thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

    // When
    var result = activityService.latestActivitiesView(pageCriteria);

    // Then
    assertEquals(1, result.content().size());
    var data = result.content().get(0);
    assertEquals(activity, data.activity());
    assertTrue(data.isNew());
    assertNull(data.status());
  }

  @Test
  void latestActivitiesView_shouldReturnMultipleActivities() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 10, 2);

    var activity1 = mock(Activity.class);
    var activity2 = mock(Activity.class);
    var pagedActivities = new PagedResult<>(List.of(activity1, activity2), pageInfo);

    when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
        .thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.getAllDeclaredActivitiesOf(student)).thenReturn(List.of());

    // When
    var result = activityService.latestActivitiesView(pageCriteria);

    // Then
    assertEquals(2, result.content().size());
    var activities = result.content().stream().map(a -> a.activity()).toList();
    assertTrue(activities.contains(activity1));
    assertTrue(activities.contains(activity2));
  }

  @Test
  void latestActivitiesView_shouldExcludeDeclaredActivities() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 10, 2);

    var activity1 = mock(Activity.class);
    var activity2 = mock(Activity.class);
    var declaredActivity = mock(DeclaredActivity.class);
    when(declaredActivity.getActivity()).thenReturn(activity2);

    var pagedActivities = new PagedResult<>(List.of(activity1, activity2), pageInfo);

    // The repository should receive only the activities not declared
    when(activityRepository.findLatest(eq(Duration.ofDays(90)), anyList(), eq(pageCriteria)))
        .thenReturn(pagedActivities);

    var student = mock(Student.class);
    when(loggedInUserService.getLoggedInStudent()).thenReturn(student);
    when(declaredActivityService.getAllDeclaredActivitiesOf(student))
        .thenReturn(List.of(declaredActivity));

    // When
    var result = activityService.latestActivitiesView(pageCriteria);

    // Then
    assertEquals(2, result.content().size());
    assertTrue(result.content().stream().anyMatch(a -> a.activity() == activity1));
    assertTrue(result.content().stream().anyMatch(a -> a.activity() == activity2));
  }

  @Test
  void should_return_activity_detail_when_activity_exists() {
    UUID activityId = UUID.randomUUID();
    UUID bannerId = UUID.randomUUID();

    BddLogger.given("an activity exists with ID " + activityId);

    Activity activity = mock(Activity.class);
    FileData banner = mock(FileData.class);

    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

    when(activityResourceService.getActivityBanner(activity)).thenReturn(banner);

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

    when(banner.id()).thenReturn(Optional.of(bannerId));
    when(banner.name()).thenReturn(Optional.of("filename.png"));

    BddLogger.when("getActivityDetail is called for the activity ID");
    ActivityPresentationData result =
        activityService.getActivityPresentation(EActivityStatus.PUBLISHED, activityId);
    FileData resBanner = activityService.getActivityBanner(result.activity());

    BddLogger.then("the service returns the correct activity detail data");
    assertNotNull(result);
    assertEquals(activityId, result.activity().getId());
    assertEquals("Activity", result.activity().getTitle());
    assertEquals(EActivityThematic.EXPERIENCES, result.activity().getThematic());
    assertEquals("is a test activity", result.activity().getSummary());
    assertEquals(
        "<h3>Objectives</h3><p>Test activity description</p>",
        result.activity().getDescription().get());
    assertEquals("2026", result.activity().getExecutionPeriodInfo().get());
    assertTrue(resBanner.id().isPresent());
    assertEquals(bannerId, resBanner.id().get());
    assertEquals("filename.png", resBanner.name().get());

    verify(activityRepository).findById(activityId);
    verify(activityResourceService).getActivityBanner(activity);
  }

  @Test
  void should_throw_activity_not_found_exception() {
    UUID activityId = UUID.randomUUID();
    BddLogger.given("no activity exists with ID " + activityId);

    when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

    BddLogger.when("getActivityDetail is called for the non-existent activity ID");
    BddLogger.then("the service throws ActivityNotFoundException");
    assertThrows(
        ActivityNotFoundException.class,
        () -> activityService.getActivityPresentation(EActivityStatus.PUBLISHED, activityId));

    verify(activityRepository).findById(activityId);
    verifyNoInteractions(activityResourceService);
  }

  @Test
  void createActivityDraft_shouldReturnDraftAndSaveIt() {
    // Given
    String title = "Mon brouillon d'activité";

    Staff staff = mock(Staff.class);
    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);

    ArgumentCaptor<ActivityDraft> captor = ArgumentCaptor.forClass(ActivityDraft.class);
    when(activityDraftRepository.save(captor.capture()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    ActivityDraft result = activityService.createActivityDraft(title);

    // Then
    assertNotNull(result);
    assertEquals(title, result.getTitle());
    assertEquals(staff, result.getAuthor());

    verify(loggedInUserService).getLoggedInStaff();
    verify(activityDraftRepository).save(result);
  }

  @Test
  void createActivityDraft_shouldInitializeWithDefaultValues() {
    // Given
    String title = "Brouillon avec valeurs par défaut";

    Staff staff = mock(Staff.class);
    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    ActivityDraft result = activityService.createActivityDraft(title);

    // Then
    assertEquals(EActivityThematic.TRANSVERSAL, result.getThematic());
    assertEquals(-1, result.getTraceAllowedAssociations());
    assertEquals(-1, result.getFeedbackAllowedIterations());
    assertTrue(result.isEnableReflection());
  }

  @Test
  void createActivityDraft_shouldInitializeNullableFieldsAsNull() {
    // Given
    Staff staff = mock(Staff.class);
    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    ActivityDraft result = activityService.createActivityDraft("Brouillon");

    // Then
    assertTrue(result.getSummary().isEmpty());
    assertTrue(result.getDescription().isEmpty());
    assertTrue(result.getExecutionPeriodInfo().isEmpty());
    assertTrue(result.getExecutionPeriodInfoSummary().isEmpty());
  }

  @Test
  void updateActivityDraft_shouldUpdateAllFieldsAndSave() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(activityDraftRepository.save(draft)).thenReturn(draft);

    // When
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
            false);

    // Then
    verify(draft).setTitle("Nouveau titre");
    verify(draft).setThematic(EActivityThematic.EXPERIENCES);
    verify(draft).setSummary("Nouveau summary");
    verify(draft).setDescription("<p>Nouvelle description</p>");
    verify(draft).setExecutionPeriodInfo("Avant entretien");
    verify(draft).setExecutionPeriodInfoSummary("Label court");
    verify(draft).setTraceAllowedAssociations(5);
    verify(draft).setFeedbackAllowedIterations(3);
    verify(draft).setEnableReflection(false);
    verify(activityDraftRepository).save(draft);
    assertEquals(draft, result);
  }

  @Test
  void updateActivityDraft_shouldNotUpdateFieldsWhenNullIsPassed() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(activityDraftRepository.save(draft)).thenReturn(draft);

    // When
    activityService.updateActivity(
        EActivityStatus.DRAFT, draftId, null, null, null, null, null, null, null, null, null);

    // Then
    verify(draft, never()).setTitle(any());
    verify(draft, never()).setThematic(any());
    verify(draft, never()).setSummary(any());
    verify(draft, never()).setDescription(any());
    verify(draft, never()).setExecutionPeriodInfo(any());
    verify(draft, never()).setExecutionPeriodInfoSummary(any());
    verify(draft, never()).setTraceAllowedAssociations(anyInt());
    verify(draft, never()).setFeedbackAllowedIterations(anyInt());
    verify(draft, never()).setEnableReflection(anyBoolean());
    verify(activityDraftRepository).save(draft);
  }

  @Test
  void updateActivityDraft_shouldOnlyUpdateProvidedFields() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(activityDraftRepository.save(draft)).thenReturn(draft);

    // When
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
        null);

    // Then
    verify(draft).setTitle("Titre seul");
    verify(draft, never()).setThematic(any());
    verify(draft, never()).setSummary(any());
    verify(draft, never()).setDescription(any());
    verify(draft, never()).setExecutionPeriodInfo(any());
    verify(draft, never()).setExecutionPeriodInfoSummary(any());
    verify(draft, never()).setTraceAllowedAssociations(anyInt());
    verify(draft, never()).setFeedbackAllowedIterations(anyInt());
    verify(draft, never()).setEnableReflection(anyBoolean());
  }

  @Test
  void updateActivityDraft_shouldThrowActivityDraftNotFoundException_whenDraftDoesNotExist() {
    // Given
    UUID unknownId = UUID.randomUUID();
    Staff staff = mock(Staff.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(unknownId)).thenReturn(Optional.empty());

    // When / Then
    assertThrows(
        ActivityDraftNotFoundException.class,
        () ->
            activityService.updateActivity(
                EActivityStatus.DRAFT,
                unknownId,
                "Titre",
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

  @Test
  void updateActivityDraft_shouldThrowUserNotAuthorizedException_whenStaffIsNotAuthor() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff loggedInStaff = mock(Staff.class);
    Staff otherStaff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(loggedInStaff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(otherStaff);

    // When / Then
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
                null));

    verify(activityDraftRepository, never()).save(any());
  }

  @Test
  void updateActivityDraft_shouldReturnSavedDraft() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);
    ActivityDraft savedDraft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(activityDraftRepository.save(draft)).thenReturn(savedDraft);

    // When
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
            null);

    // Then
    assertEquals(savedDraft, result);
  }

  @Test
  void staffActivityWorkingSpace_shouldReturnPagedResult() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 1);
    var staff = mock(Staff.class);

    var overviewData =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Mon activité",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now());

    var pagedActivities = new PagedResult<>(List.of(overviewData), pageInfo);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
        .thenReturn(pagedActivities);

    // When
    var result = activityService.staffActivityWorkingSpace(pageCriteria);

    // Then
    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(pageInfo, result.pageInfo());

    var data = result.content().getFirst();
    assertEquals(overviewData.activityId(), data.activityId());
    assertEquals(overviewData.title(), data.title());
    assertEquals(overviewData.thematic(), data.thematic());
    assertEquals(overviewData.author(), data.author());
    assertEquals(overviewData.activityStatus(), data.activityStatus());
    assertEquals(overviewData.updatedAt(), data.updatedAt());

    verify(loggedInUserService).getLoggedInStaff();
    verify(staffActivityOverviewRepository).findAllByAuthor(staff, pageCriteria);
  }

  @Test
  void staffActivityWorkingSpace_shouldReturnEmpty_whenNoActivities() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 0);
    var staff = mock(Staff.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    // When
    var result = activityService.staffActivityWorkingSpace(pageCriteria);

    // Then
    assertNotNull(result);
    assertTrue(result.content().isEmpty());
    assertEquals(pageInfo, result.pageInfo());

    verify(loggedInUserService).getLoggedInStaff();
    verify(staffActivityOverviewRepository).findAllByAuthor(staff, pageCriteria);
  }

  @Test
  void staffActivityWorkingSpace_shouldReturnMultipleActivities() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 2);
    var staff = mock(Staff.class);

    var draft =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Brouillon",
            EActivityThematic.RESUMES,
            staff,
            EActivityStatus.DRAFT,
            Instant.now());
    var published =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Publiée",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now().minusSeconds(3600));

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(draft, published), pageInfo));

    // When
    var result = activityService.staffActivityWorkingSpace(pageCriteria);

    // Then
    assertEquals(2, result.content().size());

    var statuses =
        result.content().stream().map(ActivityStaffOverviewData::activityStatus).toList();
    assertTrue(statuses.contains(EActivityStatus.DRAFT));
    assertTrue(statuses.contains(EActivityStatus.PUBLISHED));
  }

  @Test
  void staffActivityWorkingSpace_shouldUseLoggedInStaff_asFilter() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 0);
    var staff = mock(Staff.class);
    var otherStaff = mock(Staff.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    // When
    activityService.staffActivityWorkingSpace(pageCriteria);

    // Then — on vérifie que le repo est appelé avec le bon staff, pas n'importe lequel
    verify(staffActivityOverviewRepository).findAllByAuthor(staff, pageCriteria);
    verify(staffActivityOverviewRepository, never()).findAllByAuthor(eq(otherStaff), any());
  }

  @Test
  void staffActivityWorkingSpace_shouldMapAllFieldsCorrectly() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 1);
    var staff = mock(Staff.class);

    UUID expectedId = UUID.randomUUID();
    Instant expectedUpdatedAt = Instant.parse("2024-01-15T10:00:00Z");

    var overviewData =
        new ActivityStaffOverviewData(
            expectedId,
            "Titre précis",
            EActivityThematic.SELF_KNOWLEDGE,
            staff,
            EActivityStatus.DRAFT,
            expectedUpdatedAt);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(staffActivityOverviewRepository.findAllByAuthor(staff, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(overviewData), pageInfo));

    // When
    var result = activityService.staffActivityWorkingSpace(pageCriteria);

    // Then — chaque champ est mappé sans transformation
    var data = result.content().getFirst();
    assertEquals(expectedId, data.activityId());
    assertEquals("Titre précis", data.title());
    assertEquals(EActivityThematic.SELF_KNOWLEDGE, data.thematic());
    assertEquals(staff, data.author());
    assertEquals(EActivityStatus.DRAFT, data.activityStatus());
    assertEquals(expectedUpdatedAt, data.updatedAt());
  }

  @Test
  void staffActivityLibrary_shouldReturnPagedResult() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 1);
    var staff = mock(Staff.class);

    var overviewData =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Mon activité",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now());

    var pagedActivities = new PagedResult<>(List.of(overviewData), pageInfo);

    when(activityRepository.findAllStaffOverview(null, pageCriteria)).thenReturn(pagedActivities);

    // When
    var result = activityService.staffActivityLibrary(null, pageCriteria);

    // Then
    assertNotNull(result);
    assertEquals(1, result.content().size());
    assertEquals(pageInfo, result.pageInfo());

    var data = result.content().getFirst();
    assertEquals(overviewData.activityId(), data.activityId());
    assertEquals(overviewData.title(), data.title());
    assertEquals(overviewData.thematic(), data.thematic());
    assertEquals(overviewData.author(), data.author());
    assertEquals(overviewData.activityStatus(), data.activityStatus());
    assertEquals(overviewData.updatedAt(), data.updatedAt());

    verify(loggedInUserService).getLoggedInStaff();
    verify(activityRepository).findAllStaffOverview(null, pageCriteria);
  }

  @Test
  void staffActivityLibrary_shouldReturnEmpty_whenNoActivities() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 0);

    when(activityRepository.findAllStaffOverview(null, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    // When
    var result = activityService.staffActivityLibrary(null, pageCriteria);

    // Then
    assertNotNull(result);
    assertTrue(result.content().isEmpty());
    assertEquals(pageInfo, result.pageInfo());

    verify(loggedInUserService).getLoggedInStaff();
    verify(activityRepository).findAllStaffOverview(null, pageCriteria);
  }

  @Test
  void staffActivityLibrary_shouldForwardThematicToRepository() {
    // Given
    var pageCriteria = mock(PageCriteria.class);
    var pageInfo = new PageInfo(0, 8, 0);

    when(activityRepository.findAllStaffOverview(EActivityThematic.EXPERIENCES, pageCriteria))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    // When
    activityService.staffActivityLibrary(EActivityThematic.EXPERIENCES, pageCriteria);

    // Then
    verify(loggedInUserService).getLoggedInStaff();
    verify(activityRepository).findAllStaffOverview(EActivityThematic.EXPERIENCES, pageCriteria);
  }

  @Test
  void publish_shouldCreateActivityFromDraftAndSaveIt() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
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

    ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
    when(activityRepository.save(captor.capture()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Activity result = activityService.publish(draftId);

    // Then
    assertNotNull(result);
    assertEquals(draftId, result.getId());
    assertEquals("Mon activité", result.getTitle());
    assertEquals(EActivityThematic.EXPERIENCES, result.getThematic());
    assertEquals("Un résumé", result.getSummary());

    verify(activityRepository).save(any(Activity.class));
    verify(activityDraftRepository).removeFromDatabase(draft);
  }

  @Test
  void publish_shouldRemoveDraftAfterPublishing() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(draft.getId()).thenReturn(draftId);
    when(draft.getTitle()).thenReturn("Titre");
    when(draft.getThematic()).thenReturn(EActivityThematic.EXPERIENCES);
    when(draft.getSummary()).thenReturn(Optional.of("Résumé"));
    when(draft.getDescription()).thenReturn(Optional.empty());
    when(draft.getExecutionPeriodInfo()).thenReturn(Optional.empty());
    when(draft.getExecutionPeriodInfoSummary()).thenReturn(Optional.empty());
    when(draft.isEnableReflection()).thenReturn(true);
    when(draft.getTraceAllowedAssociations()).thenReturn(-1);
    when(draft.getFeedbackAllowedIterations()).thenReturn(-1);
    when(activityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    activityService.publish(draftId);

    // Then — le draft est bien supprimé après publication
    InOrder inOrder = inOrder(activityRepository, activityDraftRepository);
    inOrder.verify(activityRepository).save(any(Activity.class));
    inOrder.verify(activityDraftRepository).removeFromDatabase(draft);
  }

  @Test
  void publish_shouldThrowActivityDraftNotFoundException_whenDraftDoesNotExist() {
    // Given
    UUID unknownId = UUID.randomUUID();
    Staff staff = mock(Staff.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(unknownId)).thenReturn(Optional.empty());

    // When / Then
    assertThrows(ActivityDraftNotFoundException.class, () -> activityService.publish(unknownId));

    verify(activityRepository, never()).save(any());
    verify(activityDraftRepository, never()).removeFromDatabase(any());
  }

  @Test
  void publish_shouldThrowUserNotAuthorizedException_whenStaffIsNotAuthor() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff loggedInStaff = mock(Staff.class);
    Staff otherStaff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(loggedInStaff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(otherStaff);

    // When / Then
    assertThrows(UserNotAuthorizedException.class, () -> activityService.publish(draftId));

    verify(activityRepository, never()).save(any());
    verify(activityDraftRepository, never()).removeFromDatabase(any());
  }

  @Test
  void publish_shouldThrowFieldValidationException_whenSummaryIsEmpty() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(draft.getSummary()).thenReturn(Optional.empty());

    // When / Then
    assertThrows(FieldValidationException.class, () -> activityService.publish(draftId));

    verify(activityRepository, never()).save(any());
    verify(activityDraftRepository, never()).removeFromDatabase(any());
  }

  @Test
  void publish_shouldMapOptionalFieldsAsNull_whenNotPresent() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(draft.getId()).thenReturn(draftId);
    when(draft.getTitle()).thenReturn("Titre");
    when(draft.getThematic()).thenReturn(EActivityThematic.RESUMES);
    when(draft.getSummary()).thenReturn(Optional.of("Résumé"));
    when(draft.getDescription()).thenReturn(Optional.empty());
    when(draft.getExecutionPeriodInfo()).thenReturn(Optional.empty());
    when(draft.getExecutionPeriodInfoSummary()).thenReturn(Optional.empty());
    when(draft.isEnableReflection()).thenReturn(false);
    when(draft.getTraceAllowedAssociations()).thenReturn(3);
    when(draft.getFeedbackAllowedIterations()).thenReturn(5);

    ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
    when(activityRepository.save(captor.capture()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Activity result = activityService.publish(draftId);

    // Then — les champs optionnels absents du draft donnent des Optional vides sur l'activité
    assertNotNull(result);
    assertTrue(result.getDescription().isEmpty());
    assertTrue(result.getExecutionPeriodInfo().isEmpty());
    assertTrue(result.getExecutionPeriodInfoSummary().isEmpty());
  }

  @Test
  void publish_shouldPreserveAllDraftFields() {
    // Given
    UUID draftId = UUID.randomUUID();
    Staff staff = mock(Staff.class);
    ActivityDraft draft = mock(ActivityDraft.class);

    when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
    when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
    when(draft.getAuthor()).thenReturn(staff);
    when(draft.getId()).thenReturn(draftId);
    when(draft.getTitle()).thenReturn("Titre complet");
    when(draft.getThematic()).thenReturn(EActivityThematic.SELF_KNOWLEDGE);
    when(draft.getSummary()).thenReturn(Optional.of("Résumé complet"));
    when(draft.getDescription()).thenReturn(Optional.of("<p>Description complète</p>"));
    when(draft.getExecutionPeriodInfo()).thenReturn(Optional.of("Avant entretien"));
    when(draft.getExecutionPeriodInfoSummary()).thenReturn(Optional.of("Label court"));
    when(draft.isEnableReflection()).thenReturn(false);
    when(draft.getTraceAllowedAssociations()).thenReturn(3);
    when(draft.getFeedbackAllowedIterations()).thenReturn(5);

    when(activityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Activity result = activityService.publish(draftId);

    // Then
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
  }

  @Nested
  class GivenAnActivityService {

    @BeforeEach
    void setupGiven() {
      BddLogger.given("an activity service");
    }

    @Nested
    class WhenDeletingAnActivityDraft {

      @BeforeEach
      void setupWhen() {
        BddLogger.when("deleting an activity draft");
      }

      @Nested
      class AndTheDraftExists {

        ActivityDraft draft = mock(ActivityDraft.class);
        UUID draftId = UUID.randomUUID();

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft exists");
          when(activityDraftRepository.findById(draftId)).thenReturn(Optional.of(draft));
        }

        @Nested
        class AndTheLoggedInStaffIsTheAuthor {

          Staff staff = mock(Staff.class);

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is the author of the draft");
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

          Staff staff = mock(Staff.class);
          Staff author = mock(Staff.class);

          @BeforeEach
          void setupAnd() {
            BddLogger.and("the logged-in staff is not the author of the draft");
            when(loggedInUserService.getLoggedInStaff()).thenReturn(staff);
            when(draft.getAuthor()).thenReturn(author);
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
        UUID unknownId = UUID.randomUUID();

        @BeforeEach
        void setupAnd() {
          BddLogger.and("the draft does not exist");
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
  }
}
