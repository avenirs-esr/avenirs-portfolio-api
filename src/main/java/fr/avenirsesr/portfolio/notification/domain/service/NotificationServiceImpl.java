package fr.avenirsesr.portfolio.notification.domain.service;

import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
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
}
