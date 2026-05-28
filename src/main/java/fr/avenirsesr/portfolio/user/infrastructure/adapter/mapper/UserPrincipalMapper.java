package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserPrincipalEntity;

public class UserPrincipalMapper {
  public static final UserPrincipalMapper INSTANCE = new UserPrincipalMapper();

  public User toDomain(UserPrincipalEntity userPrincipalEntity) {
    return userPrincipalEntity != null
        ? User.toDomain(
            userPrincipalEntity.getUser().getId(),
            userPrincipalEntity.getUser().getFirstName(),
            userPrincipalEntity.getUser().getLastName(),
            userPrincipalEntity.getUser().getEmail(),
            userPrincipalEntity.getUser().getCreatedAt(),
            userPrincipalEntity.getUser().getUpdatedAt())
        : null;
  }
}
