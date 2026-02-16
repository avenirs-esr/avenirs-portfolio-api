package fr.avenirsesr.portfolio.activity.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import org.springframework.data.jpa.domain.Specification;

public class ActivitySpecification {
  public static Specification<ActivityEntity> withThematic(EActivityThematic thematic) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("thematic"), thematic);
  }
}
