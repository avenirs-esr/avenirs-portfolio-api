package fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import org.springframework.data.jpa.domain.Specification;

public class AMSSpecification {

  public static Specification<AMSEntity> belongsTo(Student student) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("user"), UserMapper.fromDomain(student));
  }
}
