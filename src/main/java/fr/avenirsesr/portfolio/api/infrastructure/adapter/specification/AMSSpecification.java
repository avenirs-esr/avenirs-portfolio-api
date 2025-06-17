package fr.avenirsesr.portfolio.api.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.CohortEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

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
}
