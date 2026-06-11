package fr.avenirsesr.portfolio.notification.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public class NotificationMapper implements Mapper<NotificationEntity, Notification> {

  public static final NotificationMapper INSTANCE = new NotificationMapper();

  @Override
  public NotificationEntity fromDomain(Notification domain) {
    return NotificationEntity.of(
        domain.getId(),
        domain.getCreatedAt(),
        domain.getUpdatedAt(),
        domain.getType(),
        domain.getElementId(),
        UserMapper.INSTANCE.fromDomain(domain.getUser()),
        domain.getUserCategory(),
        domain.getParameters(),
        domain.isSeen());
  }

  @Override
  public Notification toDomain(NotificationEntity entity) {
    return Notification.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getType(),
        entity.getElementId(),
        UserMapper.INSTANCE.toDomain(entity.getUser()),
        entity.getUserCategory(),
        entity.getParameters(),
        entity.isSeen());
  }
}
