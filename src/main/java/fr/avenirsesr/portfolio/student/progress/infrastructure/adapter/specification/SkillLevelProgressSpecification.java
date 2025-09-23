package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import org.springframework.data.jpa.domain.Specification;

public class SkillLevelProgressSpecification {
  public static Specification<SkillLevelProgressEntity> ofAms(AMSEntity ams) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(ams, root.get("amses"));
  }
}
