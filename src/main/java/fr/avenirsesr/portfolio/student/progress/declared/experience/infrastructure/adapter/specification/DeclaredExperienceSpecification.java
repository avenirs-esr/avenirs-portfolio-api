package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class DeclaredExperienceSpecification {
  public static Specification<DeclaredExperienceEntity> ordered() {
    return (root, query, cb) -> {
      if (query == null) {
        return cb.conjunction();
      }

      List<Order> orders = new ArrayList<>();

      Expression<Integer> endDateOrder =
          cb.<Integer>selectCase().when(cb.isNull(root.get("endDate")), 1).otherwise(0);

      orders.add(cb.desc(endDateOrder));
      orders.add(cb.desc(root.get("endDate")));
      orders.add(cb.asc(cb.lower(root.get("title"))));

      query.orderBy(orders);

      return cb.conjunction();
    };
  }

  public static Specification<DeclaredExperienceEntity> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
    };
  }

  public static Specification<DeclaredExperienceEntity> isValorized(Boolean isValorized) {
    return (root, query, criteriaBuilder) -> {
      if (isValorized == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("valorized"), isValorized);
    };
  }

  public static Specification<DeclaredExperienceEntity> hasExperienceType(
      EExperienceType experienceType) {
    return (root, query, criteriaBuilder) -> {
      if (experienceType == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("experienceType"), experienceType);
    };
  }
}
