package fr.avenirsesr.portfolio.notification.domain.service;

import fr.avenirsesr.portfolio.notification.domain.exception.NotificationNotFoundException;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;

  @Override
  public void notify(BaseNotification notification) {
    // Todo: check if user has enabled notifications
    notificationRepository.save(notification.build());
    log.debug("[{}] created", notification);
  }

  @Override
  public void markAsSeen(UUID id) {
    log.debug("Marking notification [{}] as seen", id);
    Notification notification =
        notificationRepository.findById(id).orElseThrow(NotificationNotFoundException::new);
    notification.setSeen(true);
    notificationRepository.save(notification);
  }
}
