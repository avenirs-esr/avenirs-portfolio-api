package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import jakarta.persistence.criteria.Join;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class StudentProgressSpecification {

  public static Specification<StudentProgressEntity> isAPC() {
    return (root, query, cb) -> {
      Join<Object, Object> programJoin = root.join("trainingPath").join("program");
      return cb.equal(programJoin.get("isAPC"), true);
    };
  }

  public static Specification<StudentProgressEntity> hasSkillLevelProgresses(
      List<SkillLevelProgress> skillLevelProgresses) {
    List<SkillLevelProgressEntity> skillLevelProgressEntities =
        skillLevelProgresses.stream().map(SkillLevelProgressMapper.INSTANCE::fromDomain).toList();
    return (root, query, criteriaBuilder) -> {
      Join<StudentProgressEntity, SkillLevelProgressEntity> skillLevelProgressJoin =
          root.join("skillLevels");
      return skillLevelProgressJoin.in(skillLevelProgressEntities);
    };
  }
}
