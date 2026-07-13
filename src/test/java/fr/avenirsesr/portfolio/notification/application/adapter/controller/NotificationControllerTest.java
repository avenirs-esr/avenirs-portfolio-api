package fr.avenirsesr.portfolio.notification.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.application.adapter.dto.NotificationDTO;
import fr.avenirsesr.portfolio.notification.domain.exception.NotificationNotFoundException;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.domain.model.notification.parameters.AskForFeedbackParameters;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

  @Mock private NotificationService notificationService;

  @InjectMocks private NotificationController controller;

  private Principal principal;

  @BeforeEach
  void setUp() {
    principal = () -> "0a8700ab-90b6-4a38-8338-acbdd4fbcd3d";
  }

  @Test
  void markAsSeen_should_return_204_and_delegate_to_service() {
    BddLogger.given("A valid notification ID");
    UUID id = UUID.randomUUID();
    doNothing().when(notificationService).markAsSeen(id);

    BddLogger.when("markAsSeen is called");
    ResponseEntity<Void> response = controller.markAsSeen(principal, id);

    BddLogger.then("204 No Content is returned and the service is called with the correct ID");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    assertThat(response.getBody()).isNull();
    verify(notificationService).markAsSeen(id);
  }

  @Test
  void markAsSeen_should_call_service_exactly_once() {
    BddLogger.given("A valid notification ID");
    UUID id = UUID.randomUUID();
    doNothing().when(notificationService).markAsSeen(id);

    BddLogger.when("markAsSeen is called");
    controller.markAsSeen(principal, id);

    BddLogger.then("The service is called exactly once with no extra interactions");
    verify(notificationService, times(1)).markAsSeen(id);
    verifyNoMoreInteractions(notificationService);
  }

  @Test
  void markAsSeen_should_propagate_NotificationNotFoundException_from_service() {
    BddLogger.given("A notification ID that does not exist");
    UUID id = UUID.randomUUID();
    doThrow(new NotificationNotFoundException()).when(notificationService).markAsSeen(id);

    BddLogger.when("markAsSeen is called");
    BddLogger.then("The exception propagates to the error handler");
    org.junit.jupiter.api.Assertions.assertThrows(
        NotificationNotFoundException.class, () -> controller.markAsSeen(principal, id));
  }

  @Test
  void getNotifications_should_return_200_with_paged_response() {
    BddLogger.given("A service returning one notification for the STAFF category");
    Notification notif = mock(Notification.class);
    UUID notifId = UUID.randomUUID();
    when(notif.getId()).thenReturn(notifId);
    when(notif.getCreatedAt()).thenReturn(Instant.parse("2026-01-01T10:00:00Z"));
    when(notif.getType()).thenReturn(ENotificationType.ASK_FOR_FEEDBACK);
    when(notif.getElementId()).thenReturn(UUID.randomUUID());
    when(notif.getParameters())
        .thenReturn(new AskForFeedbackParameters("Alice", "Martin", "titre"));
    when(notif.isSeen()).thenReturn(false);

    when(notificationService.getNotifications(eq(EUserCategory.STAFF), any(PageCriteria.class)))
        .thenReturn(new PagedResult<>(List.of(notif), new PageInfo(0, 8, 1)));

    BddLogger.when("GET /me/notifications/STAFF is called with default pagination");
    ResponseEntity<PagedResponse<NotificationDTO>> response =
        controller.getNotifications(principal, EUserCategory.STAFF, null, null);

    BddLogger.then("200 OK is returned with the notification in the data list");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).hasSize(1);
    assertThat(response.getBody().data().get(0).id()).isEqualTo(notifId);
    assertThat(response.getBody().data().get(0).type())
        .isEqualTo(ENotificationType.ASK_FOR_FEEDBACK);
    assertThat(response.getBody().page().totalElements()).isEqualTo(1L);
  }

  @Test
  void getNotifications_should_delegate_category_and_pagination_to_service() {
    BddLogger.given("A request with category STUDENT, page 2 and pageSize 5");
    when(notificationService.getNotifications(any(), any()))
        .thenReturn(new PagedResult<>(List.of(), new PageInfo(2, 5, 15)));

    BddLogger.when("getNotifications is called");
    controller.getNotifications(principal, EUserCategory.STUDENT, 2, 5);

    BddLogger.then("The service receives exactly the STUDENT category and the correct pagination");
    ArgumentCaptor<PageCriteria> criteriaCaptor = forClass(PageCriteria.class);
    verify(notificationService)
        .getNotifications(eq(EUserCategory.STUDENT), criteriaCaptor.capture());
    assertThat(criteriaCaptor.getValue().page()).isEqualTo(2);
    assertThat(criteriaCaptor.getValue().pageSize()).isEqualTo(5);
  }

  @Test
  void getNotifications_should_return_empty_data_when_service_returns_empty_page() {
    BddLogger.given("A service returning an empty page");
    when(notificationService.getNotifications(any(), any()))
        .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

    BddLogger.when("getNotifications is called");
    ResponseEntity<PagedResponse<NotificationDTO>> response =
        controller.getNotifications(principal, EUserCategory.STAFF, null, null);

    BddLogger.then("200 OK is returned with an empty data list and zero totalElements");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().data()).isEmpty();
    assertThat(response.getBody().page().totalElements()).isZero();
  }

  @Test
  void getNotifications_should_call_service_exactly_once() {
    BddLogger.given("Any valid request");
    when(notificationService.getNotifications(any(), any()))
        .thenReturn(new PagedResult<>(List.of(), new PageInfo(0, 8, 0)));

    BddLogger.when("getNotifications is called once");
    controller.getNotifications(principal, EUserCategory.STAFF, null, null);

    BddLogger.then("The service is called exactly once with no other interactions");
    verify(notificationService, times(1)).getNotifications(any(), any());
    verifyNoMoreInteractions(notificationService);
  }
}
