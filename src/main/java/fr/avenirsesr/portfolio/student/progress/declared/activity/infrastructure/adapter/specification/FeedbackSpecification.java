package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.FeedbackEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class FeedbackSpecification {

  private FeedbackSpecification() {}

  public static Specification<FeedbackEntity> hasStaffAuthor(UUID staffId) {
    return (root, query, cb) ->
        cb.equal(root.join("declaredActivity").join("activity").get("author").get("id"), staffId);
  }

  public static Specification<FeedbackEntity> hasStatus(EFeedbackStatus status) {
    return (root, query, cb) -> {
      if (status == null) return cb.conjunction();
      return cb.equal(root.get("status"), status);
    };
  }

  public static Specification<FeedbackEntity> hasActivityId(UUID activityId) {
    return (root, query, cb) -> {
      if (activityId == null) return cb.conjunction();
      return cb.equal(root.get("declaredActivity").get("activity").get("id"), activityId);
    };
  }
}
