package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class SelfKnowledgeElementSpecification {

  public static Specification<SelfKnowledgeElementEntity> hasStudentId(UUID studentId) {
    return (root, query, cb) ->
        studentId == null ? null : cb.equal(root.get("student").get("id"), studentId);
  }

  public static Specification<SelfKnowledgeElementEntity> hasSelfKnowledgeCategoryId(
      UUID categoryId) {
    return (root, query, cb) ->
        categoryId == null
            ? null
            : cb.equal(root.get("selfKnowledgeCategory").get("id"), categoryId);
  }
}
