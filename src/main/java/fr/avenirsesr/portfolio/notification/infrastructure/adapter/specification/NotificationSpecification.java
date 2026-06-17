package fr.avenirsesr.portfolio.notification.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.notification.infrastructure.adapter.model.NotificationEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class NotificationSpecification {

  private NotificationSpecification() {}

  public static Specification<NotificationEntity> hasUser(UUID userId) {
    return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
  }

  public static Specification<NotificationEntity> hasUserCategoryNullOrEquals(
      EUserCategory userCategory) {
    return (root, query, cb) ->
        cb.or(root.get("userCategory").isNull(), cb.equal(root.get("userCategory"), userCategory));
  }

  public static Specification<NotificationEntity> isNotSeen() {
    return (root, query, cb) -> cb.isFalse(root.get("seen"));
  }
}
