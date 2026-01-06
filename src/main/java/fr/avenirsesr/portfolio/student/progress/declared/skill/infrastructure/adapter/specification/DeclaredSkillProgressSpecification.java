package fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.mapper.DeclaredSkillMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import java.util.UUID;
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
}
