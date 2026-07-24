package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model.DeclaredProgramEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class DeclaredProgramSpecification {
  public static Sort toSort(SortCriteria sortCriteria) {
    if (sortCriteria == null) {
      return Sort.unsorted();
    }

    Sort.Direction direction =
        sortCriteria.order() == ESortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

    return switch (sortCriteria.field()) {
      case NAME -> Sort.by(direction, "title");
      case DATE -> Sort.by(direction, "endDate");
    };
  }

  public static Specification<DeclaredProgramEntity> isValorized(Boolean isValorized) {
    return (root, query, criteriaBuilder) -> {
      if (isValorized == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("valorized"), isValorized);
    };
  }
}
