package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ActivitySpecification {
  public static Specification<ActivityEntity> isPublished() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("status"), EActivityStatus.PUBLISHED);
  }

  public static Specification<ActivityEntity> withThematic(EActivityThematic thematic) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("thematic"), thematic);
  }

  public static Specification<ActivityEntity> latest(Duration duration) {
    return (root, query, criteriaBuilder) -> {
      Instant threshold = Instant.now().minus(duration);
      return criteriaBuilder.greaterThan(root.get("createdAt"), threshold);
    };
  }

  public static Specification<ActivityEntity> exclude(List<ActivityEntity> activities) {
    return (root, query, criteriaBuilder) -> {
      if (activities == null || activities.isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return root.in(activities).not();
    };
  }

  public static Specification<ActivityEntity> visibleByInstitutions(List<UUID> institutionIds) {
    return visibleByTarget("targetInstitutionIds", institutionIds);
  }

  public static Specification<ActivityEntity> visibleByGroups(List<UUID> groupIds) {
    return visibleByTarget("targetGroupIds", groupIds);
  }

  private static Specification<ActivityEntity> visibleByTarget(
      String targetAttribute, List<UUID> studentIds) {
    return (root, query, cb) -> {
      Predicate noTarget = cb.isEmpty(root.get(targetAttribute));
      if (studentIds == null || studentIds.isEmpty()) {
        return noTarget;
      }

      Subquery<UUID> subquery = query.subquery(UUID.class);
      Root<ActivityEntity> subRoot = subquery.from(ActivityEntity.class);
      Join<ActivityEntity, UUID> targetJoin = subRoot.join(targetAttribute);
      subquery.select(targetJoin).where(cb.equal(subRoot, root), targetJoin.in(studentIds));

      return cb.or(noTarget, cb.exists(subquery));
    };
  }

  public static Specification<ActivityEntity> hasFeedback(
      UUID authorId, EFeedbackStatus... statuses) {
    return (root, query, cb) -> {
      Subquery<Integer> subquery = query.subquery(Integer.class);
      Root<FeedbackEntity> feedback = subquery.from(FeedbackEntity.class);
      Join<FeedbackEntity, DeclaredActivityEntity> declaredActivity =
          feedback.join("declaredActivity");

      List<Predicate> predicates = new ArrayList<>(2);
      predicates.add(cb.equal(declaredActivity.get("activity"), root));
      predicates.add(cb.equal(root.get("author").get("id"), authorId));

      if (statuses != null && statuses.length > 0) {
        predicates.add(feedback.get("status").in((Object[]) statuses));
      }

      subquery.select(cb.literal(1)).where(predicates.toArray(Predicate[]::new));

      return cb.exists(subquery);
    };
  }
}
