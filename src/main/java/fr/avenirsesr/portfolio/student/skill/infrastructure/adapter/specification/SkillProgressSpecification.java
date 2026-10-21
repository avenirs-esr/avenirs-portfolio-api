package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.SkillProgressEntity;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class SkillProgressSpecification {
  public static Specification<SkillProgressEntity> skillProgressAlreadyExists(
      UUID skillId, UUID studentId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get("skill").get("id"), skillId),
            criteriaBuilder.equal(root.get("student").get("id"), studentId));
  }

  public static Specification<SkillProgressEntity> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("skill").get("title")), "%" + keyword.toLowerCase() + "%");
    };
  }

  public static Specification<SkillProgressEntity> isValorized(Boolean isValorized) {
    return (root, query, criteriaBuilder) -> {
      if (isValorized == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("valorized"), isValorized);
    };
  }

  public static Sort toSort(SortCriteria sortCriteria) {
    if (sortCriteria == null) {
      return Sort.unsorted();
    }

    Sort.Direction direction =
        sortCriteria.order() == ESortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

    return switch (sortCriteria.field()) {
      case NAME -> Sort.by(direction, "skill.title");
      case DATE -> Sort.by(direction, "createdAt");
    };
  }
}
