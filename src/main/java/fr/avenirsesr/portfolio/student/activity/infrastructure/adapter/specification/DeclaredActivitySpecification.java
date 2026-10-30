package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityAuthorizationEntity;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EActivityAssessmentStatus;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EDeclaredActivityStatus;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

  public static Specification<DeclaredActivityEntity> hasStatusIn(
      Collection<EDeclaredActivityStatus> statuses) {
    return (root, query, cb) -> {
      if (statuses == null || statuses.isEmpty()) {
        return cb.conjunction();
      }
      return cb.or(
          statuses.stream()
              .map(status -> statusPredicate(status, root, query, cb))
              .toArray(Predicate[]::new));
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
      Collection<EActivityAssessmentStatus> statuses) {
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

  private static Predicate isActive(Root<DeclaredActivityEntity> root, CriteriaBuilder cb) {
    return cb.and(cb.isNull(root.get("unsubscribedAt")), cb.isNull(root.get("finishedAt")));
  }

  private static Predicate isStarted(Root<DeclaredActivityEntity> root, CriteriaBuilder cb) {
    var startedAt = root.<Instant>get("startedAt");
    return cb.and(cb.isNotNull(startedAt), cb.lessThan(startedAt, Instant.now()));
  }

  private static Predicate hasActiveFeedback(
      Root<DeclaredActivityEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    var feedbacks = query.subquery(Integer.class);
    var feedback = feedbacks.from(FeedbackEntity.class);
    feedbacks
        .select(cb.literal(1))
        .where(
            cb.equal(feedback.get("declaredActivity").get("id"), root.get("id")),
            feedback.get("status").in(List.of(EFeedbackStatus.NEW, EFeedbackStatus.IN_PROCESS)));
    return cb.exists(feedbacks);
  }

  private static Predicate isSubmittedForAssessment(
      Root<DeclaredActivityEntity> root, CriteriaBuilder cb) {
    return cb.isNotNull(root.get("submittedAt"));
  }

  private static Predicate isSubmittedForFeedback(
      Root<DeclaredActivityEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    return cb.and(cb.not(isSubmittedForAssessment(root, cb)), hasActiveFeedback(root, query, cb));
  }

  private static Predicate isSubmitted(
      Root<DeclaredActivityEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    return cb.or(isSubmittedForAssessment(root, cb), hasActiveFeedback(root, query, cb));
  }

  /** Mirrors DeclaredActivityServiceImpl#getDeclaredActivityStatus. */
  private static Predicate statusPredicate(
      EDeclaredActivityStatus status,
      Root<DeclaredActivityEntity> root,
      CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    return switch (status) {
      case UNSUBSCRIBED -> cb.isNotNull(root.get("unsubscribedAt"));
      case COMPLETED ->
          cb.and(cb.isNull(root.get("unsubscribedAt")), cb.isNotNull(root.get("finishedAt")));
      case SUBMITTED_FOR_ASSESSMENT ->
          cb.and(isActive(root, cb), isSubmittedForAssessment(root, cb));
      case SUBMITTED_FOR_FEEDBACK ->
          cb.and(isActive(root, cb), isSubmittedForFeedback(root, query, cb));
      case IN_PROGRESS ->
          cb.and(isActive(root, cb), isStarted(root, cb), cb.not(isSubmitted(root, query, cb)));
      case SUBSCRIBED ->
          cb.and(
              isActive(root, cb),
              cb.not(isStarted(root, cb)),
              cb.not(isSubmitted(root, query, cb)));
      case CONSULTED -> cb.disjunction();
    };
  }

  private static Predicate assessmentStatusPredicate(
      EActivityAssessmentStatus status, Root<DeclaredActivityEntity> root, CriteriaBuilder cb) {
    var assessedAt = root.get("assessment").get("assessedAt");
    var staffOpenedAt = root.get("assessment").get("staffOpenedAt");
    return switch (status) {
      case NEW -> cb.and(cb.isNull(assessedAt), cb.isNull(staffOpenedAt));
      case OPENED -> cb.and(cb.isNull(assessedAt), cb.isNotNull(staffOpenedAt));
      case SENT -> cb.isNotNull(assessedAt);
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
