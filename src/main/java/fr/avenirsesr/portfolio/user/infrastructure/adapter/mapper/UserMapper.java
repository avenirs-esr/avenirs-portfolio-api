package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public interface UserMapper {
  static UserEntity fromDomain(User user) {
    return user != null
        ? UserEntity.of(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail())
        : null;
  }

  static User toDomain(UserEntity userEntity) {
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
