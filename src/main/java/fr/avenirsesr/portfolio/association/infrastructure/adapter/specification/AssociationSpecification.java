package fr.avenirsesr.portfolio.association.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.association.domain.data.AssociationData;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.AssociationEntity;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class AssociationSpecification {
  public static Specification<AssociationEntity> in(List<AssociationData> associations) {
    return (root, query, criteriaBuilder) -> {
      if (associations == null || associations.isEmpty()) {
        return criteriaBuilder.disjunction();
      }

      List<Predicate> predicates = new ArrayList<>();

      for (AssociationData association : associations) {
        Predicate id1Match = criteriaBuilder.equal(root.get("id1"), association.id1());
        Predicate id2Match = criteriaBuilder.equal(root.get("id2"), association.id2());
        Predicate associationTypeMatch =
            criteriaBuilder.equal(root.get("associationType"), association.associationType());

        predicates.add(criteriaBuilder.and(id1Match, id2Match, associationTypeMatch));
      }

      return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<AssociationEntity> withTypeIn(
      List<EAssociationType> associationTypes) {
    return (root, query, criteriaBuilder) -> {
      if (associationTypes == null || associationTypes.isEmpty()) {
        return criteriaBuilder.disjunction();
      }

      return criteriaBuilder.or(
          associationTypes.stream()
              .map(type -> criteriaBuilder.equal(root.get("associationType"), type))
              .toList()
              .toArray(new Predicate[0]));
    };
  }

  public static Specification<AssociationEntity> key1(UUID id1) {
    return (root, query, criteriaBuilder) -> {
      return criteriaBuilder.equal(root.get("id1"), id1);
    };
  }

  public static Specification<AssociationEntity> key2(UUID id2) {
    return (root, query, criteriaBuilder) -> {
      return criteriaBuilder.equal(root.get("id2"), id2);
    };
  }

  public static Specification<AssociationEntity> key1In(Set<UUID> ids) {
    return (root, query, cb) -> root.get("id1").in(ids);
  }

  public static Specification<AssociationEntity> key2In(Set<UUID> ids) {
    return (root, query, cb) -> root.get("id2").in(ids);
  }
}
