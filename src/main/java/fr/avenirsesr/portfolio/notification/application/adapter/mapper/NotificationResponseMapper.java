package fr.avenirsesr.portfolio.notification.application.adapter.mapper;

import fr.avenirsesr.portfolio.notification.application.adapter.dto.NotificationDTO;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;

public class NotificationResponseMapper {

  public static final NotificationResponseMapper INSTANCE = new NotificationResponseMapper();

  private NotificationResponseMapper() {}

  public NotificationDTO toDTO(Notification notification) {
    return new NotificationDTO(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getType(),
        notification.getElementId(),
        notification.getParameters(),
        notification.isSeen());
  }
}
