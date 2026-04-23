package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class StudentProgressSpecification {

  public static Specification<StudentProgressEntity> isAPC() {
    return (root, query, cb) -> {
      Join<Object, Object> programJoin = root.join("trainingPath").join("program");
      return cb.equal(programJoin.get("isAPC"), true);
    };
  }
}
