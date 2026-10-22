package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ActivityAuthorizationSpecification {
  public static <T> Specification<T> withEntityIdIn(
      String entityIdAttribute, Collection<UUID> ids) {
    return (root, query, cb) -> root.get(entityIdAttribute).in(ids);
  }

  public static <T> Specification<T> withStaffId(UUID staffId) {
    return (root, query, cb) -> cb.equal(root.get("staff").get("id"), staffId);
  }

  public static <T> Specification<T> withRight(EActivityRight right) {
    return (root, query, cb) -> cb.isMember(right, root.<Set<EActivityRight>>get("rights"));
  }
}
