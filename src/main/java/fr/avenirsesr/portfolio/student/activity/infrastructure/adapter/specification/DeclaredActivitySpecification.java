package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityViewEntity;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.specification.StudentSpecification;
import java.time.Instant;
import java.util.Collection;
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
}
