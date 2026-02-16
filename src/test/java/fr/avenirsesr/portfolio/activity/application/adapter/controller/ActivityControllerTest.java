package fr.avenirsesr.portfolio.activity.application.adapter.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityOverviewDTO;
import fr.avenirsesr.portfolio.activity.domain.data.ActivityWithStudentStatusData;
import fr.avenirsesr.portfolio.activity.domain.model.Activity;
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
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ActivityControllerTest {

  @Mock private ActivityService activityService;

  @InjectMocks private ActivityController controller;

  private UUID userId;
  private User user;
  private Activity activity;
  private Principal principal;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = UserFixture.create().withId(userId).toModel();
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
}
