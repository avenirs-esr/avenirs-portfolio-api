package fr.avenirsesr.portfolio.user.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor
public final class StudentSpecification {
  public static <T> Specification<T> hasStudent(Student student) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("student"), StudentMapper.INSTANCE.fromDomain(student));
  }
}
