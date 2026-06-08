package fr.avenirsesr.portfolio.notification.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationServiceImpl service;

  @Test
  void notify_should_save_the_notification_built_from_the_base_notification() {
    BddLogger.given("A BaseNotification that builds a specific Notification");
    Notification expectedNotification = mock(Notification.class);
    BaseNotification baseNotification = mock(BaseNotification.class);
    when(baseNotification.build()).thenReturn(expectedNotification);

    BddLogger.when("notify is called");
    service.notify(baseNotification);

    BddLogger.then("The repository saves exactly the notification produced by build()");
    ArgumentCaptor<Notification> captor = forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    assertThat(captor.getValue()).isSameAs(expectedNotification);
  }

  @Test
  void notify_should_call_build_exactly_once() {
    BddLogger.given("A BaseNotification mock");
    BaseNotification baseNotification = mock(BaseNotification.class);
    when(baseNotification.build()).thenReturn(mock(Notification.class));

    BddLogger.when("notify is called");
    service.notify(baseNotification);

    BddLogger.then("build() is invoked exactly once");
    verify(baseNotification, times(1)).build();
  }
}
