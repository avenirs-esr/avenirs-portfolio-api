package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import org.springframework.data.jpa.domain.Specification;

public class SelfKnowledgeCategorySpecification {
  public static Specification<SelfKnowledgeCategoryEntity> hasStudent(StudentEntity student) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);
      return criteriaBuilder.equal(root.join("students"), student);
    };
  }
}
