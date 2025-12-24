package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;

public class ExternalUserMapper implements Mapper<ExternalUserEntity, ExternalUser> {
  public static final ExternalUserMapper INSTANCE = new ExternalUserMapper();

  @Override
  public ExternalUserEntity fromDomain(ExternalUser externalUser) {
    return ExternalUserEntity.of(
        externalUser.getId(),
        externalUser.getExternalId(),
        externalUser.getSource(),
        UserMapper.INSTANCE.fromDomain(externalUser.getUser()),
        externalUser.getCategory(),
        externalUser.getEmail(),
        externalUser.getFirstName(),
        externalUser.getLastName());
  }

  @Override
  public ExternalUser toDomain(ExternalUserEntity externalUserEntity) {
    return ExternalUser.toDomain(
        externalUserEntity.getId(),
        externalUserEntity.getCreatedAt(),
        externalUserEntity.getUpdatedAt(),
        UserMapper.INSTANCE.toDomain(externalUserEntity.getUser()),
        externalUserEntity.getExternalId(),
        externalUserEntity.getSource(),
        externalUserEntity.getCategory(),
        externalUserEntity.getEmail(),
        externalUserEntity.getFirstName(),
        externalUserEntity.getLastName());
  }
}
