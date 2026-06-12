package fr.avenirsesr.portfolio.notification.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import java.util.UUID;

public interface NotificationRepository extends GenericRepositoryPort<Notification> {
  PagedResult<Notification> findByUserAndCategory(
      UUID userId, EUserCategory userCategory, PageCriteria pageCriteria);
}
