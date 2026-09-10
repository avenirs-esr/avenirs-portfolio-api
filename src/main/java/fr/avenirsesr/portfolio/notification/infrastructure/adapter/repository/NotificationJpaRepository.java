package fr.avenirsesr.portfolio.notification.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.notification.domain.model.enums.ENotificationType;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository
    extends JpaRepository<NotificationEntity, UUID>, JpaSpecificationExecutor<NotificationEntity> {

  @Modifying
  @Query("delete from NotificationEntity n where n.type = :type and n.elementId = :elementId")
  void deleteByTypeAndElementId(
      @Param("type") ENotificationType type, @Param("elementId") UUID elementId);
}
