package fr.avenirsesr.portfolio.user.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public final class StudentOwnershipSpecification {
  public static <T> Specification<T> hasStudent(Student student, String pathToStudent) {
    return (root, query, criteriaBuilder) -> {
      var from = (From<?, ?>) root;

      for (String part : pathToStudent.split("\\.")) {
        from = from.join(part);
      }

      return criteriaBuilder.equal(from, StudentMapper.fromDomain(student));
    };
  }

  public static <T> Specification<T> hasStudent(Student student) {
    return hasStudent(student, "student");
  }

  public static <T> Specification<T> hasNotStudent(Student student) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);

      Subquery<Long> sub = query.subquery(Long.class);
      Class<T> entityClass = (Class<T>) root.getModel().getBindableJavaType();
      Root<T> subRoot = sub.from(entityClass);

      Join<T, StudentEntity> subStudents = subRoot.join("students");

      sub.select(criteriaBuilder.literal(1L))
          .where(
              criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
              criteriaBuilder.equal(
                  subStudents.get("id"), StudentMapper.fromDomain(student).getId()));

      return criteriaBuilder.not(criteriaBuilder.exists(sub));
    };
  }

  public static <T> Specification<T> hasNotStudent(Student student, String pathToStudents) {
    return (root, query, criteriaBuilder) -> {
      assert query != null;
      query.distinct(true);

      var subquery = query.subquery(Long.class);
      Root<T> subRoot = subquery.from((Class<T>) root.getModel().getBindableJavaType());

      var pathParts = pathToStudents.split("\\.");
      var from = (jakarta.persistence.criteria.From<?, ?>) subRoot;

      for (String part : pathParts) {
        from = from.join(part);
      }

      var subStudents = (Join<?, StudentEntity>) from;

      subquery
          .select(criteriaBuilder.literal(1L))
          .where(
              criteriaBuilder.equal(subRoot.get("id"), root.get("id")),
              criteriaBuilder.equal(
                  subStudents.get("id"), StudentMapper.fromDomain(student).getId()));

      return criteriaBuilder.not(criteriaBuilder.exists(subquery));
    };
  }
}
