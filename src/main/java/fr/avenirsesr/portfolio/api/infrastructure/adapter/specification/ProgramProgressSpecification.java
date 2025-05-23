package fr.avenirsesr.portfolio.api.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class ProgramProgressSpecification {
  public static Specification<ProgramProgressEntity> hasStudent(UserEntity student) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("student"), student);
  }

  public static Specification<ProgramProgressEntity> hasLearningMethod(
      EPortfolioType learningMethod) {
    return (root, query, cb) -> {
      Join<Object, Object> programJoin = root.join("program");
      return cb.equal(programJoin.get("learningMethod"), learningMethod);
    };
  }
}
