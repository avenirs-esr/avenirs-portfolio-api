package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.*;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public final class DeclaredActivitySpecification {
  public static Specification<DeclaredActivityEntity> hasActivityIdIn(
      Collection<UUID> activityIds) {
    return (root, query, cb) -> root.get("activity").get("id").in(activityIds);
  }

  public static Specification<DeclaredActivityEntity> hasActivityIdInAndStudent(
      Collection<UUID> activityIds, Student student) {
    return Specification.where(StudentSpecification.<DeclaredActivityEntity>hasStudent(student))
        .and(hasActivityIdIn(activityIds));
  }

  public static Specification<DeclaredActivityEntity> hasActivityId(UUID activityId) {
    return (root, query, cb) -> cb.equal(root.get("activity").get("id"), activityId);
  }

  public static Specification<DeclaredActivityEntity> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("activity").get("title")),
          "%" + keyword.toLowerCase() + "%");
    };
  }

  public static Specification<DeclaredActivityEntity> has(String field, List<?> values) {
    return (root, query, criteriaBuilder) -> {
      if (values == null || values.isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      var activity = root.join("activity");
      return activity.get(field).in(values);
    };
  }

  public static Specification<DeclaredActivityEntity> isNotCompleted() {
    return (root, query, cb) -> cb.isNull(root.get("finishedAt"));
  }

  public static Specification<DeclaredActivityEntity> isCompleted() {
    return (root, query, cb) -> cb.isNotNull(root.get("finishedAt"));
  }

  public static Specification<DeclaredActivityEntity> isNotUnsubscribed() {
    return (root, query, cb) -> cb.isNull(root.get("unsubscribedAt"));
  }

  public static Specification<DeclaredActivityEntity> hasNotBeenViewedSince(Instant since) {
    return (root, query, cb) -> {
      var recentViews = query.subquery(UUID.class);
      var view = recentViews.from(ActivityViewEntity.class);
      recentViews
          .select(view.get("id"))
          .where(
              cb.equal(view.get("activityId"), root.get("activity").get("id")),
              cb.equal(view.get("studentId"), root.get("student").get("id")),
              cb.greaterThanOrEqualTo(view.<Instant>get("lastViewedAt"), since));
      return cb.not(cb.exists(recentViews));
    };
  }

  public static Specification<DeclaredActivityEntity> isSubmittedForAssessment() {
    return (root, query, cb) -> cb.isNotNull(root.get("submittedAt"));
  }

  public static Specification<DeclaredActivityEntity> activityHasStaffRight(
      UUID staffId, EActivityRight right) {
    return (root, query, cb) -> {
      var authorizations = query.subquery(Integer.class);
      var authorization = authorizations.from(ActivityAuthorizationEntity.class);
      authorizations
          .select(cb.literal(1))
          .where(
              cb.equal(authorization.get("activityId"), root.get("activity").get("id")),
              cb.equal(authorization.get("staff").get("id"), staffId),
              cb.isMember(right, authorization.<Set<EActivityRight>>get("rights")));
      return cb.exists(authorizations);
    };
  }

  public static Specification<DeclaredActivityEntity> hasAssessmentStatusIn(
      Collection<EDeclaredActivityStatus> statuses) {
    return (root, query, cb) -> {
      if (statuses == null || statuses.isEmpty()) {
        return cb.conjunction();
      }
      return cb.or(
          statuses.stream()
              .map(status -> assessmentStatusPredicate(status, root, cb))
              .toArray(Predicate[]::new));
    };
  }

  private static Predicate assessmentStatusPredicate(
      EDeclaredActivityStatus status, Root<DeclaredActivityEntity> root, CriteriaBuilder cb) {
    return switch (status) {
      case SUBMITTED -> cb.isNotNull(root.get("submittedAt"));
      case CONSULTED, SUBSCRIBED, IN_PROGRESS, COMPLETED, UNSUBSCRIBED -> cb.disjunction();
    };
  }

  public static Specification<DeclaredActivityEntity> isValorized(Boolean isValorized) {
    return (root, query, criteriaBuilder) -> {
      if (isValorized == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("valorized"), isValorized);
    };
  }
}
