package fr.avenirsesr.portfolio.file.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.ActivityBannerEntity;
import org.springframework.data.jpa.domain.Specification;

public interface ActivityBannerSpecification {
  static Specification<ActivityBannerEntity> ofActivity(Activity activity) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(
                root.get("activity"), ActivityMapper.INSTANCE.fromDomain(activity)));
  }

  static Specification<ActivityBannerEntity> onlyActiveVersion() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("isActiveVersion"), true);
  }
}
