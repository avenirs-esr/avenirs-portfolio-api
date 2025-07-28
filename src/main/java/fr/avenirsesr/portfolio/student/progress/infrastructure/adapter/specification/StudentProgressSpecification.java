package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.Join;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class StudentProgressSpecification {
  public static Specification<StudentProgressEntity> hasStudent(UserEntity student) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("student"), student);
  }

  public static Specification<StudentProgressEntity> isAPC() {
    return (root, query, cb) -> {
      Join<Object, Object> programJoin = root.join("trainingPath").join("program");
      return cb.equal(programJoin.get("isAPC"), true);
    };
  }

  public static Specification<StudentProgressEntity> hasSkillLevelProgresses(
      List<SkillLevelProgressEntity> skillLevelProgresses) {
    return (root, query, criteriaBuilder) -> {
      Join<StudentProgressEntity, SkillLevelProgressEntity> skillLevelProgressJoin =
          root.join("skillLevels");
      return skillLevelProgressJoin.in(skillLevelProgresses);
    };
  }
}
