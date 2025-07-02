package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ProgramProgressSpecification {
  public static Specification<ProgramProgressEntity> hasStudent(UserEntity student) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("student"), student);
  }

  public static Specification<ProgramProgressEntity> isAPC() {
    return (root, query, cb) -> {
      Join<Object, Object> programJoin = root.join("program");
      return cb.equal(programJoin.get("isAPC"), true);
    };
  }
}
