package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityEntity;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
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
}
