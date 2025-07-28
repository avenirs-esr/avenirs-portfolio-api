package fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class AMSSpecification {

  public static Specification<AMSEntity> belongsToUserViaCohorts(UUID userId) {
    return (root, query, criteriaBuilder) -> {
      if (query != null) {
        query.distinct(true);
      }

      Join<AMSEntity, CohortEntity> cohortJoin = root.join("cohorts", JoinType.INNER);
      Join<CohortEntity, UserEntity> userJoin = cohortJoin.join("users", JoinType.INNER);

      return criteriaBuilder.equal(userJoin.get("id"), userId);
    };
  }

  public static Specification<AMSEntity> hasSkillLevelProgress(
      List<SkillLevelProgressEntity> skillLevelProgresses) {
    return (root, query, criteriaBuilder) -> {
      if (skillLevelProgresses == null || skillLevelProgresses.isEmpty()) {
        return criteriaBuilder.conjunction();
      }

      if (query != null) {
        query.distinct(true);
      }

      Subquery<UUID> subquery = query.subquery(UUID.class);
      Root<AMSEntity> amsRoot = subquery.from(AMSEntity.class);
      Join<AMSEntity, SkillLevelProgressEntity> skillJoin = amsRoot.join("skillLevels");

      subquery.select(amsRoot.get("id")).where(skillJoin.in(skillLevelProgresses));

      return criteriaBuilder.in(root.get("id")).value(subquery);
    };
  }
}
