package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class SelfKnowledgeCategorySpecification {

  public static Specification<SelfKnowledgeCategoryEntity> hasStudent(StudentEntity student) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);
      return criteriaBuilder.equal(root.join("students"), student);
    };
  }

  public static Specification<SelfKnowledgeCategoryEntity> hasNotStudent(StudentEntity student) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);

      Subquery<Long> sub = query.subquery(Long.class);
      var subRoot = sub.from(SelfKnowledgeCategoryEntity.class);
      Join<SelfKnowledgeCategoryEntity, StudentEntity> subStudents = subRoot.join("students");

      sub.select(criteriaBuilder.literal(1L))
          .where(
              criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
              criteriaBuilder.equal(subStudents.get("id"), student.getId()));

      return criteriaBuilder.not(criteriaBuilder.exists(sub));
    };
  }
}
