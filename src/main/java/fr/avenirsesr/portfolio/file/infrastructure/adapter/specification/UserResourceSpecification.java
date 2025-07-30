package fr.avenirsesr.portfolio.file.infrastructure.adapter.specification;

import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public interface UserResourceSpecification {
  static Specification<UserPhotoEntity> ofUser(
      UserEntity user, EUserCategory userCategory, EUserPhotoType type) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get("user"), user),
            criteriaBuilder.equal(root.get("userCategory"), userCategory),
            criteriaBuilder.equal(root.get("userPhotoType"), type));
  }

  static Specification<UserPhotoEntity> onlyActiveVersion() {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("isActiveVersion"), true);
  }
}
