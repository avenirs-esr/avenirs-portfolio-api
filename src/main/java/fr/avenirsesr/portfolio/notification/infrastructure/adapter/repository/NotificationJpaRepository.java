package fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationJpaRepository
    extends JpaRepository<NotificationEntity, UUID>, JpaSpecificationExecutor<NotificationEntity> {}
