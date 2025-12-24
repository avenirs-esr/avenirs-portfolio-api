package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class SelfKnowledgeCategorySpecification {

  public static Specification<SelfKnowledgeCategoryEntity> hasStudent(Student student) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);
      return criteriaBuilder.equal(
          root.join("students"), StudentMapper.INSTANCE.fromDomain(student));
    };
  }

  public static Specification<SelfKnowledgeCategoryEntity> hasNotStudent(Student student) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);

      Subquery<Long> sub = query.subquery(Long.class);
      var subRoot = sub.from(SelfKnowledgeCategoryEntity.class);
      Join<SelfKnowledgeCategoryEntity, StudentEntity> subStudents = subRoot.join("students");

      sub.select(criteriaBuilder.literal(1L))
          .where(
              criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
              criteriaBuilder.equal(
                  subStudents.get("id"), StudentMapper.INSTANCE.fromDomain(student).getId()));

      return criteriaBuilder.not(criteriaBuilder.exists(sub));
    };
  }
}
