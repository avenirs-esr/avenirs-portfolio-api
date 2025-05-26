package fr.avenirsesr.portfolio.api.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class TrackSpecification {
  public static Specification<TrackEntity> ofUser(UserEntity user) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
  }
}
