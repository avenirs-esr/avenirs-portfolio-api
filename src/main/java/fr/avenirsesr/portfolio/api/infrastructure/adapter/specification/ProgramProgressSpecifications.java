package fr.avenirsesr.portfolio.api.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import jakarta.persistence.criteria.JoinType;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ProgramProgressSpecifications {
  public static Specification<ProgramProgressEntity> findByUserId(UUID userId) {
    return (root, query, cb) -> {
      root.fetch("student", JoinType.LEFT);
      query.distinct(true);
      return cb.equal(root.get("student").get("id"), userId);
    };
  }
}
