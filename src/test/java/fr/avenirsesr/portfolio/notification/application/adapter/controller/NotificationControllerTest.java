package fr.avenirsesr.portfolio.notification.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.exception.NotificationNotFoundException;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import java.security.Principal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
