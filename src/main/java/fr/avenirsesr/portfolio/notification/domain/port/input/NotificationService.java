package fr.avenirsesr.portfolio.notification.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.notification.domain.model.Notification;
import fr.avenirsesr.portfolio.notification.domain.model.notification.BaseNotification;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
  void notify(BaseNotification notification);

  void notifyAll(List<? extends BaseNotification> notifications);

  void markAsSeen(UUID id);

  PagedResult<Notification> getNotifications(EUserCategory userCategory, PageCriteria pageCriteria);
}
