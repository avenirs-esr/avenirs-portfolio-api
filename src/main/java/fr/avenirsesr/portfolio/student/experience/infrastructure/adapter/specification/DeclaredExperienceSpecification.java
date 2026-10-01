package fr.avenirsesr.portfolio.student.experience.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.experience.infrastructure.adapter.model.DeclaredExperienceEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class DeclaredExperienceSpecification {
  public static Sort toSort(SortCriteria... sortCriterias) {
    if (sortCriterias == null || sortCriterias.length == 0) {
      return Sort.unsorted();
    }

    return Arrays.stream(sortCriterias)
        .filter(Objects::nonNull)
        .map(
            sortCriteria -> {
              Sort.Direction direction =
                  sortCriteria.order() == ESortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

              return switch (sortCriteria.field()) {
                case NAME -> Sort.by(direction, "title");
                case DATE -> Sort.by(direction, "endDate", "startDate");
              };
            })
        .reduce(Sort.unsorted(), Sort::and);
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
      List<EExperienceType> experienceTypes) {
    return (root, query, criteriaBuilder) -> {
      if (experienceTypes == null || experienceTypes.isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return root.get("experienceType").in(experienceTypes);
    };
  }
}
