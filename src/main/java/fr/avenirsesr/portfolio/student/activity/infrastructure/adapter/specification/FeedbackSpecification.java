package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class FeedbackSpecification {

  private FeedbackSpecification() {}

  public static Specification<FeedbackEntity> isAssignedToStaff(UUID staffId) {
    return (root, query, cb) -> {
      var subquery = query.subquery(Integer.class);
      var authorization = subquery.from(ActivityAuthorizationEntity.class);
      subquery
          .select(cb.literal(1))
          .where(
              cb.equal(
                  authorization.get("activityId"),
                  root.get("declaredActivity").get("activity").get("id")),
              cb.equal(authorization.get("staff").get("id"), staffId),
              cb.isMember(
                  EActivityRight.FEEDBACK, authorization.<Set<EActivityRight>>get("rights")));
      return cb.exists(subquery);
    };
  }

  public static Specification<FeedbackEntity> hasStatus(EFeedbackStatus... status) {
    List<EFeedbackStatus> statuses =
        status == null ? List.of() : Arrays.stream(status).filter(Objects::nonNull).toList();
    return (root, query, cb) -> {
      if (statuses.isEmpty()) return cb.conjunction();
      return root.get("status").in(statuses);
    };
  }

  public static Specification<FeedbackEntity> hasActivityId(UUID activityId) {
    return (root, query, cb) -> {
      if (activityId == null) return cb.conjunction();
      return cb.equal(root.get("declaredActivity").get("activity").get("id"), activityId);
    };
  }

  public static Specification<FeedbackEntity> hasDeclaredActivityId(UUID declaredActivityId) {
    return (root, query, cb) ->
        cb.equal(root.get("declaredActivity").get("id"), declaredActivityId);
  }

  public static Specification<FeedbackEntity> isLatestOfItsDeclaredActivity() {
    return (root, query, cb) -> {
      var subquery = query.subquery(Instant.class);
      var subRoot = subquery.from(FeedbackEntity.class);
      subquery.select(cb.greatest(subRoot.<Instant>get("createdAt")));
      subquery.where(
          cb.equal(
              subRoot.get("declaredActivity").get("id"), root.get("declaredActivity").get("id")));
      return cb.equal(root.<Instant>get("createdAt"), subquery);
    };
  }
}
