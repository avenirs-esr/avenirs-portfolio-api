package fr.avenirsesr.portfolio.activity.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.output.repository.ActivityRepository;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.port.input.DeclaredActivityService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ActivityServiceImplTest {

  @Mock private ActivityRepository activityRepository;
  @Mock private LoggedInUserService loggedInUserService;
  @Mock private DeclaredActivityService declaredActivityService;

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
    String executionPeriodInfo = "2026";

    // When
    Activity createdActivity =
        activityService.create(id, title, thematic, summary, executionPeriodInfo);

    // Then
    assertNotNull(createdActivity);
    assertEquals(id, createdActivity.getId());
    assertEquals(title, createdActivity.getTitle());
    assertEquals(thematic, createdActivity.getThematic());
    assertEquals(summary, createdActivity.getSummary());
    assertEquals(executionPeriodInfo, createdActivity.getExecutionPeriodInfo());

    // Verify repository was called
    ArgumentCaptor<Activity> captor = ArgumentCaptor.forClass(Activity.class);
    verify(activityRepository).save(captor.capture());
    Activity savedActivity = captor.getValue();
    assertEquals(createdActivity, savedActivity);
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
}
