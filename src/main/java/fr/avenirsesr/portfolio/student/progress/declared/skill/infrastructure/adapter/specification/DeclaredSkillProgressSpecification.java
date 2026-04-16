package fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.mapper.DeclaredSkillMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class DeclaredSkillProgressSpecification {
  public static Specification<DeclaredSkillProgressEntity> declaredSkillProgressAlreadyExists(
      DeclaredSkill declaredSkill, UUID studentId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(
                root.get("declaredSkill"), DeclaredSkillMapper.INSTANCE.fromDomain(declaredSkill)),
            criteriaBuilder.equal(root.get("student").get("id"), studentId));
  }

  public static Specification<DeclaredSkillProgressEntity> search(String keyword) {
    return (root, query, criteriaBuilder) -> {
      if (keyword == null || keyword.trim().isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("declaredSkill").get("libelle")),
          "%" + keyword.toLowerCase() + "%");
    };
  }

  public static Sort toSort(SortCriteria sortCriteria) {
    if (sortCriteria == null) {
      return Sort.unsorted();
    }

    Sort.Direction direction =
        sortCriteria.order() == ESortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

    return switch (sortCriteria.field()) {
      case NAME -> Sort.by(direction, "declaredSkill.libelle");
      case DATE -> Sort.by(direction, "createdAt");
      default ->
          throw new IllegalArgumentException("Unsupported sort field: " + sortCriteria.field());
    };
  }
}
