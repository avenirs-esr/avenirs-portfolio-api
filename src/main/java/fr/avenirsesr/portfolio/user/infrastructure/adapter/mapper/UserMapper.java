package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public class UserMapper implements Mapper<UserEntity, User> {
  public static final UserMapper INSTANCE = new UserMapper();

  @Override
  public UserEntity fromDomain(User user) {
    return user != null
        ? UserEntity.of(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt())
        : null;
  }

  @Override
  public User toDomain(UserEntity userEntity) {
    return userEntity != null
        ? User.toDomain(
            userEntity.getId(),
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getEmail(),
            userEntity.getCreatedAt(),
            userEntity.getUpdatedAt())
        : null;
  }
}
