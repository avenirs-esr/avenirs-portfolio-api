package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;

public interface ExternalUserMapper {
  static ExternalUserEntity fromDomain(ExternalUser externalUser) {
    return ExternalUserEntity.of(
        externalUser.getId(),
        externalUser.getExternalId(),
        externalUser.getSource(),
        UserMapper.fromDomain(externalUser.getUser()),
        externalUser.getCategory(),
        externalUser.getEmail(),
        externalUser.getFirstName(),
        externalUser.getLastName());
  }

  static ExternalUser toDomain(ExternalUserEntity externalUserEntity) {
    return ExternalUser.toDomain(
        externalUserEntity.getId(),
        externalUserEntity.getCreatedAt(),
        externalUserEntity.getUpdatedAt(),
        UserMapper.toDomain(externalUserEntity.getUser()),
        externalUserEntity.getExternalId(),
        externalUserEntity.getSource(),
        externalUserEntity.getCategory(),
        externalUserEntity.getEmail(),
        externalUserEntity.getFirstName(),
        externalUserEntity.getLastName());
  }
}
