package fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class TraceSpecification {
  public static Specification<TraceEntity> ofUser(UserEntity user) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
  }

  public static Specification<TraceEntity> unassociated() {
    return (root, query, criteriaBuilder) -> {
      Subquery<SkillLevelEntity> skillLevelSubquery = query.subquery(SkillLevelEntity.class);
      Root<TraceEntity> skillLevelSubRoot = skillLevelSubquery.from(TraceEntity.class);
      Join<TraceEntity, SkillLevelEntity> skillLevelJoin = skillLevelSubRoot.join("skillLevels");

      skillLevelSubquery
          .select(skillLevelJoin)
          .where(criteriaBuilder.equal(skillLevelSubRoot.get("id"), root.get("id")));

      Subquery<AMSEntity> amsSubquery = query.subquery(AMSEntity.class);
      Root<TraceEntity> amsSubRoot = amsSubquery.from(TraceEntity.class);
      Join<TraceEntity, AMSEntity> amsJoin = amsSubRoot.join("amses");

      amsSubquery
          .select(amsJoin)
          .where(criteriaBuilder.equal(amsSubRoot.get("id"), root.get("id")));

      Predicate noSkillLevels = criteriaBuilder.not(criteriaBuilder.exists(skillLevelSubquery));
      Predicate noAmses = criteriaBuilder.not(criteriaBuilder.exists(amsSubquery));

      query.distinct(true);

      return criteriaBuilder.and(noSkillLevels, noAmses);
    };
  }
}
