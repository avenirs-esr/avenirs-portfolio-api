package fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.mapper.NotificationMapper;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDatabaseRepository
    extends GenericJpaRepositoryAdapter<Notification, NotificationEntity>
    implements NotificationRepository {

  public NotificationDatabaseRepository(NotificationJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, NotificationEntity.class, NotificationMapper.INSTANCE);
  }
}
