package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class SelfKnowledgeElementSpecification {

  public static Specification<SelfKnowledgeElementEntity> hasStudentId(UUID studentId) {
    return (root, query, cb) ->
        studentId == null ? null : cb.equal(root.get("student").get("id"), studentId);
  }

  public static Specification<SelfKnowledgeElementEntity> hasSelfKnowledgeCategoryIn(
      List<ESelfKnowledgeCategory> selfKnowledgeCategories) {
    return (root, query, cb) ->
        selfKnowledgeCategories == null || selfKnowledgeCategories.isEmpty()
            ? null
            : root.get("selfKnowledgeCategory").in(selfKnowledgeCategories);
  }

  public static Specification<SelfKnowledgeElementEntity> isValorized(Boolean isValorized) {
    return (root, query, criteriaBuilder) -> {
      if (isValorized == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("valorized"), isValorized);
    };
  }
}
