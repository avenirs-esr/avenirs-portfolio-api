package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityStaffOverviewDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.dto.AuthorDTO;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityOverviewDtoMapper;
import fr.avenirsesr.portfolio.activity.application.adapter.mapper.ActivityStaffOverviewDtoMapper;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityStaffOverviewData;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.domain.port.input.ActivityService;
import fr.avenirsesr.portfolio.activity.infrastructure.fixture.ActivityFixture;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ActivityControllerTest {

  @Mock private ActivityService activityService;
  @Mock private ActivityOverviewDtoMapper activityOverviewDtoMapper;
  @Mock private ActivityStaffOverviewDtoMapper activityStaffOverviewDtoMapper;

  @InjectMocks private ActivityController controller;

  private UUID userId;
  private User user;
  private Activity activity;
  private Principal principal;
  private static AuthorDTO AUTHOR;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserFixture.create().withId(userId).toModel();
    AUTHOR = new AuthorDTO(userId, "staff firstname", "staff lastname");
    activity = ActivityFixture.create().toModel();
    principal = () -> userId.toString();
  }

  @Test
  void shouldReturnActivitiesViewWithoutThematic() {
    BddLogger.given("an ActivityController");

    PageInfo pageInfo = new PageInfo(0, 10, 1);

    ActivityWithStudentStatusData data =
        new ActivityWithStudentStatusData(activity, true, EDeclaredActivityStatus.SUBSCRIBED);

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        new PagedResult<>(List.of(data), pageInfo);

    when(activityService.activitiesView(eq(null), any(PageCriteria.class))).thenReturn(pagedResult);
    when(activityOverviewDtoMapper.toDTO(data))
        .thenReturn(
            new ActivityOverviewDTO(
                activity.getId(),
                AUTHOR,
                null,
                null,
                EDeclaredActivityStatus.SUBSCRIBED,
                null,
                null,
                false));

    BddLogger.when("getting activities view without thematic");
    ResponseEntity<PagedResponse<ActivityOverviewDTO>> response =
        controller.getActivitiesView(principal, 0, 10, null);

    BddLogger.then("it should return the activities view");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    ActivityOverviewDTO dto = response.getBody().data().getFirst();
    assertEquals(activity.getId(), dto.id());
    assertEquals(EDeclaredActivityStatus.SUBSCRIBED, dto.status());

    verify(activityService).activitiesView(eq(null), any(PageCriteria.class));
  }

  @Test
  void shouldReturnActivitiesViewWithThematic() {
    BddLogger.given("an ActivityController with thematic filter");

    PageInfo pageInfo = new PageInfo(0, 10, 1);

    ActivityWithStudentStatusData data = new ActivityWithStudentStatusData(activity, true, null);

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        new PagedResult<>(List.of(data), pageInfo);

    when(activityService.activitiesView(eq(EActivityThematic.EXPERIENCES), any(PageCriteria.class)))
        .thenReturn(pagedResult);
    when(activityOverviewDtoMapper.toDTO(data))
        .thenReturn(
            new ActivityOverviewDTO(activity.getId(), AUTHOR, null, null, null, null, null, false));

    BddLogger.when("getting activities view with thematic");
    var response = controller.getActivitiesView(principal, 0, 10, EActivityThematic.EXPERIENCES);

    BddLogger.then("it should return filtered activities");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    ActivityOverviewDTO dto = response.getBody().data().getFirst();
    assertEquals(activity.getId(), dto.id());
    assertNull(dto.status());

    verify(activityService)
        .activitiesView(eq(EActivityThematic.EXPERIENCES), any(PageCriteria.class));
  }

  @Test
  void shouldReturnEmptyActivitiesView() {
    BddLogger.given("an ActivityController with no activities");

    PageInfo pageInfo = new PageInfo(0, 10, 0);

    PagedResult<ActivityWithStudentStatusData> pagedResult = new PagedResult<>(List.of(), pageInfo);

    when(activityService.activitiesView(any(), any(PageCriteria.class))).thenReturn(pagedResult);

    BddLogger.when("getting activities view with no results");
    var response = controller.getActivitiesView(principal, 0, 10, null);

    BddLogger.then("it should return empty content");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().data().isEmpty());

    verify(activityService).activitiesView(any(), any(PageCriteria.class));
  }

  @Test
  void shouldReturnLatestActivitiesView() {
    BddLogger.given("an ActivityController for latest activities");

    PageInfo pageInfo = new PageInfo(0, 10, 1);

    ActivityWithStudentStatusData data = new ActivityWithStudentStatusData(activity, false, null);

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        new PagedResult<>(List.of(data), pageInfo);

    when(activityService.latestActivitiesView(any(PageCriteria.class))).thenReturn(pagedResult);
    when(activityOverviewDtoMapper.toDTO(data))
        .thenReturn(
            new ActivityOverviewDTO(activity.getId(), AUTHOR, null, null, null, null, null, false));

    BddLogger.when("getting latest activities view");
    var response = controller.getLatestActivitiesView(principal, 0, 10);

    BddLogger.then("it should return latest activities with correct data");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    ActivityOverviewDTO dto = response.getBody().data().getFirst();
    assertEquals(activity.getId(), dto.id());
    assertNull(dto.status()); // latest activities have no status

    verify(activityService).latestActivitiesView(any(PageCriteria.class));
  }

  @Test
  void shouldReturnLatestActivitiesViewWithSingleActivity() {
    BddLogger.given("an ActivityController for latest activities");

    PageInfo pageInfo = new PageInfo(0, 10, 1);

    ActivityWithStudentStatusData data = new ActivityWithStudentStatusData(activity, false, null);

    PagedResult<ActivityWithStudentStatusData> pagedResult =
        new PagedResult<>(List.of(data), pageInfo);

    when(activityService.latestActivitiesView(any(PageCriteria.class))).thenReturn(pagedResult);
    when(activityOverviewDtoMapper.toDTO(data))
        .thenReturn(
            new ActivityOverviewDTO(activity.getId(), AUTHOR, null, null, null, null, null, false));

    BddLogger.when("getting latest activities view");
    var response = controller.getLatestActivitiesView(principal, 0, 10);

    BddLogger.then("it should return latest activities with correct data");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    ActivityOverviewDTO dto = response.getBody().data().getFirst();
    assertEquals(activity.getId(), dto.id());
    assertNull(dto.status()); // latest activities have no status

    verify(activityService).latestActivitiesView(any(PageCriteria.class));
  }

  @Test
  void shouldReturnLatestActivitiesViewWithMultipleActivities() {
    BddLogger.given("an ActivityController with multiple latest activities");

    PageInfo pageInfo = new PageInfo(0, 10, 2);

    Activity activity2 = ActivityFixture.create().toModel();

    List<ActivityWithStudentStatusData> dataList =
        List.of(
            new ActivityWithStudentStatusData(activity, false, null),
            new ActivityWithStudentStatusData(activity2, false, null));

    PagedResult<ActivityWithStudentStatusData> pagedResult = new PagedResult<>(dataList, pageInfo);

    when(activityService.latestActivitiesView(any(PageCriteria.class))).thenReturn(pagedResult);
    when(activityOverviewDtoMapper.toDTO(dataList.get(0)))
        .thenReturn(
            new ActivityOverviewDTO(activity.getId(), AUTHOR, null, null, null, null, null, false));
    when(activityOverviewDtoMapper.toDTO(dataList.get(1)))
        .thenReturn(
            new ActivityOverviewDTO(
                activity2.getId(), AUTHOR, null, null, null, null, null, false));

    BddLogger.when("getting latest activities view");
    var response = controller.getLatestActivitiesView(principal, 0, 10);

    BddLogger.then("it should return all latest activities");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().data().size());

    List<UUID> ids = response.getBody().data().stream().map(ActivityOverviewDTO::id).toList();
    assertTrue(ids.contains(activity.getId()));
    assertTrue(ids.contains(activity2.getId()));

    verify(activityService).latestActivitiesView(any(PageCriteria.class));
  }

  @Test
  void shouldReturnEmptyLatestActivitiesView() {
    BddLogger.given("an ActivityController with no latest activities");

    PageInfo pageInfo = new PageInfo(0, 10, 0);

    PagedResult<ActivityWithStudentStatusData> pagedResult = new PagedResult<>(List.of(), pageInfo);

    when(activityService.latestActivitiesView(any(PageCriteria.class))).thenReturn(pagedResult);

    BddLogger.when("getting latest activities view with no results");
    var response = controller.getLatestActivitiesView(principal, 0, 10);

    BddLogger.then("it should return empty content");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().data().isEmpty());

    verify(activityService).latestActivitiesView(any(PageCriteria.class));
  }

  @Test
  void shouldReturnStaffActivityWorkingSpace() {
    BddLogger.given("an ActivityController for staff working space");

    PageInfo pageInfo = new PageInfo(0, 8, 1);
    var staff = mock(Staff.class);

    var overviewData =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Mon activité",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now());

    var pagedResult = new PagedResult<>(List.of(overviewData), pageInfo);

    when(activityService.staffActivityWorkingSpace(any(PageCriteria.class)))
        .thenReturn(pagedResult);

    var expectedDto =
        new ActivityStaffOverviewDTO(
            overviewData.activityId(),
            AUTHOR,
            "title",
            EActivityThematic.EXPERIENCES,
            EActivityStatus.PUBLISHED,
            Instant.now());
    when(activityStaffOverviewDtoMapper.toDTO(overviewData)).thenReturn(expectedDto);

    BddLogger.when("getting staff activity working space");
    var response = controller.getStaffActivityWorkingSpace(principal, 0, 8);

    BddLogger.then("it should return the working space with correct data");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    var dto = response.getBody().data().getFirst();
    assertEquals(overviewData.activityId(), dto.activityId());
    assertEquals(EActivityStatus.PUBLISHED, dto.activityStatus());

    verify(activityService).staffActivityWorkingSpace(any(PageCriteria.class));
    verify(activityStaffOverviewDtoMapper).toDTO(overviewData);
  }

  @Test
  void shouldReturnEmptyStaffActivityWorkingSpace() {
    BddLogger.given("an ActivityController with no activities in working space");

    PageInfo pageInfo = new PageInfo(0, 8, 0);

    when(activityService.staffActivityWorkingSpace(any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    BddLogger.when("getting staff activity working space with no results");
    var response = controller.getStaffActivityWorkingSpace(principal, 0, 8);

    BddLogger.then("it should return empty content");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().data().isEmpty());

    verify(activityService).staffActivityWorkingSpace(any(PageCriteria.class));
    verifyNoInteractions(activityStaffOverviewDtoMapper);
  }

  @Test
  void shouldReturnStaffActivityWorkingSpaceWithDraftAndPublished() {
    BddLogger.given("an ActivityController with both draft and published activities");

    PageInfo pageInfo = new PageInfo(0, 8, 2);
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

    when(activityService.staffActivityWorkingSpace(any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(draft, published), pageInfo));

    when(activityStaffOverviewDtoMapper.toDTO(draft))
        .thenReturn(
            new ActivityStaffOverviewDTO(
                draft.activityId(),
                AUTHOR,
                "title",
                EActivityThematic.RESUMES,
                EActivityStatus.DRAFT,
                Instant.now()));
    when(activityStaffOverviewDtoMapper.toDTO(published))
        .thenReturn(
            new ActivityStaffOverviewDTO(
                published.activityId(),
                AUTHOR,
                "title",
                EActivityThematic.EXPERIENCES,
                EActivityStatus.PUBLISHED,
                Instant.now()));

    BddLogger.when("getting staff activity working space");
    var response = controller.getStaffActivityWorkingSpace(principal, 0, 8);

    BddLogger.then("it should return both draft and published activities");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().data().size());

    var statuses =
        response.getBody().data().stream().map(ActivityStaffOverviewDTO::activityStatus).toList();
    assertTrue(statuses.contains(EActivityStatus.DRAFT));
    assertTrue(statuses.contains(EActivityStatus.PUBLISHED));

    verify(activityService).staffActivityWorkingSpace(any(PageCriteria.class));
    verify(activityStaffOverviewDtoMapper).toDTO(draft);
    verify(activityStaffOverviewDtoMapper).toDTO(published);
  }

  @Test
  void shouldForwardPageCriteriaToService() {
    BddLogger.given("an ActivityController receiving specific pagination params");

    PageInfo pageInfo = new PageInfo(2, 5, 0);

    when(activityService.staffActivityWorkingSpace(any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    BddLogger.when("getting staff activity working space with page=2 and pageSize=5");
    controller.getStaffActivityWorkingSpace(principal, 2, 5);

    BddLogger.then("it should forward the pagination params to the service");

    verify(activityService)
        .staffActivityWorkingSpace(
            argThat(criteria -> criteria.page() == 2 && criteria.pageSize() == 5));
  }

  @Test
  void shouldReturnStaffActivityLibrary() {
    BddLogger.given("an ActivityController for staff library");

    PageInfo pageInfo = new PageInfo(0, 8, 1);
    var staff = mock(Staff.class);

    var overviewData =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Mon activité",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now());

    var pagedResult = new PagedResult<>(List.of(overviewData), pageInfo);

    when(activityService.staffActivityLibrary(eq(null), any(PageCriteria.class)))
        .thenReturn(pagedResult);

    var expectedDto =
        new ActivityStaffOverviewDTO(
            overviewData.activityId(),
            AUTHOR,
            "title",
            EActivityThematic.EXPERIENCES,
            EActivityStatus.PUBLISHED,
            Instant.now());
    when(activityStaffOverviewDtoMapper.toDTO(overviewData)).thenReturn(expectedDto);

    BddLogger.when("getting staff activity library");
    var response = controller.getStaffActivityLibrary(principal, 0, 8, null);

    BddLogger.then("it should return the library with correct data");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    var dto = response.getBody().data().getFirst();
    assertEquals(overviewData.activityId(), dto.activityId());
    assertEquals(EActivityStatus.PUBLISHED, dto.activityStatus());

    verify(activityService).staffActivityLibrary(eq(null), any(PageCriteria.class));
    verify(activityStaffOverviewDtoMapper).toDTO(overviewData);
  }

  @Test
  void shouldReturnStaffActivityLibraryWithThematic() {
    BddLogger.given("an ActivityController for staff library with thematic filter");

    PageInfo pageInfo = new PageInfo(0, 8, 1);
    var staff = mock(Staff.class);

    var overviewData =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "Mon activité",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now());

    var pagedResult = new PagedResult<>(List.of(overviewData), pageInfo);

    when(activityService.staffActivityLibrary(
            eq(EActivityThematic.EXPERIENCES), any(PageCriteria.class)))
        .thenReturn(pagedResult);

    when(activityStaffOverviewDtoMapper.toDTO(overviewData))
        .thenReturn(
            new ActivityStaffOverviewDTO(
                overviewData.activityId(),
                AUTHOR,
                "title",
                EActivityThematic.EXPERIENCES,
                EActivityStatus.PUBLISHED,
                Instant.now()));

    BddLogger.when("getting staff activity library with thematic");
    var response =
        controller.getStaffActivityLibrary(principal, 0, 8, EActivityThematic.EXPERIENCES);

    BddLogger.then("it should return filtered activities");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().data().size());

    verify(activityService)
        .staffActivityLibrary(eq(EActivityThematic.EXPERIENCES), any(PageCriteria.class));
  }

  @Test
  void shouldReturnEmptyStaffActivityLibrary() {
    BddLogger.given("an ActivityController with no activities in library");

    PageInfo pageInfo = new PageInfo(0, 8, 0);

    when(activityService.staffActivityLibrary(any(), any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    BddLogger.when("getting staff activity library with no results");
    var response = controller.getStaffActivityLibrary(principal, 0, 8, null);

    BddLogger.then("it should return empty content");

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().data().isEmpty());

    verify(activityService).staffActivityLibrary(any(), any(PageCriteria.class));
    verifyNoInteractions(activityStaffOverviewDtoMapper);
  }

  @Test
  void shouldForwardPageCriteriaToLibraryService() {
    BddLogger.given("an ActivityController receiving specific pagination params");

    PageInfo pageInfo = new PageInfo(2, 5, 0);

    when(activityService.staffActivityLibrary(any(), any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(), pageInfo));

    BddLogger.when("getting staff activity library with page=2 and pageSize=5");
    controller.getStaffActivityLibrary(principal, 2, 5, null);

    BddLogger.then("it should forward the pagination params to the service");

    verify(activityService)
        .staffActivityLibrary(
            any(), argThat(criteria -> criteria.page() == 2 && criteria.pageSize() == 5));
  }

  @Test
  void shouldApplyMapperToEachItem() {
    BddLogger.given("an ActivityController with multiple activities to map");

    PageInfo pageInfo = new PageInfo(0, 8, 3);
    var staff = mock(Staff.class);

    var data1 =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "A1",
            EActivityThematic.EXPERIENCES,
            staff,
            EActivityStatus.PUBLISHED,
            Instant.now());
    var data2 =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "A2",
            EActivityThematic.RESUMES,
            staff,
            EActivityStatus.DRAFT,
            Instant.now());
    var data3 =
        new ActivityStaffOverviewData(
            UUID.randomUUID(),
            "A3",
            EActivityThematic.SELF_KNOWLEDGE,
            staff,
            EActivityStatus.DRAFT,
            Instant.now());

    when(activityService.staffActivityWorkingSpace(any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(data1, data2, data3), pageInfo));

    when(activityStaffOverviewDtoMapper.toDTO(any()))
        .thenReturn(
            new ActivityStaffOverviewDTO(
                UUID.randomUUID(), AUTHOR, null, null, EActivityStatus.DRAFT, Instant.now()));

    BddLogger.when("getting staff activity working space");
    var response = controller.getStaffActivityWorkingSpace(principal, 0, 8);

    BddLogger.then("the mapper should be called once per item");

    assertEquals(3, response.getBody().data().size());
    verify(activityStaffOverviewDtoMapper).toDTO(data1);
    verify(activityStaffOverviewDtoMapper).toDTO(data2);
    verify(activityStaffOverviewDtoMapper).toDTO(data3);
  }
}
