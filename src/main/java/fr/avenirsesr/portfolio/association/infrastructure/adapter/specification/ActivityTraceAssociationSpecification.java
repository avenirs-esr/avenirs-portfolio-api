package fr.avenirsesr.portfolio.association.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.ActivityTraceAssociationEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ActivityTraceAssociationSpecification {
  public static Specification<ActivityTraceAssociationEntity> in(
      List<ActivityTraceAssociationData> associations) {
    return (root, query, criteriaBuilder) -> {
      if (associations == null || associations.isEmpty()) {
        return criteriaBuilder.disjunction();
      }

      List<Predicate> predicates = new ArrayList<>();

      for (ActivityTraceAssociationData association : associations) {
        Predicate activityMatch =
            criteriaBuilder.equal(root.get("activity").get("id"), association.declaredActivityId());

        Predicate traceMatch =
            criteriaBuilder.equal(root.get("trace").get("id"), association.traceId());

        predicates.add(criteriaBuilder.and(activityMatch, traceMatch));
      }

      return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    };
  }
}
