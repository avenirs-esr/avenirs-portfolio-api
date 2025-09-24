package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import org.springframework.data.jpa.domain.Specification;

public class SkillLevelProgressSpecification {
  public static Specification<SkillLevelProgressEntity> linkedTo(AMSEntity ams) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(ams, root.get("amses"));
  }

  public static Specification<SkillLevelProgressEntity> with(Student student) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("student"), UserMapper.fromDomain(student));
  }
}
