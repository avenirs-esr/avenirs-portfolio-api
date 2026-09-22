package fr.avenirsesr.portfolio.student.experience.infrastructure.adapter.resolver;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import org.springframework.data.domain.Sort;

public class DeclaredExperienceSortResolver {

  public static Sort toSort(SortCriteria sortCriteria) {
    if (sortCriteria == null) {
      return Sort.by(Sort.Direction.DESC, "startDate");
    }

    Sort.Direction direction =
        sortCriteria.order() == ESortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

    return switch (sortCriteria.field()) {
      case NAME -> Sort.by(direction, "title");
      case DATE -> Sort.by(direction, "startDate");
    };
  }
}
