package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface UserMapper {
  static UserEntity fromModelToEntity(User user) {
    return new UserEntity();
  }

  static User fromEntityToModel(UserEntity userEntity) {
    return User.toDomain(
        userEntity.getId(),
        userEntity.getFirstName(),
        userEntity.getLastName(),
        userEntity.getEmail(),
        null,
        null);
  }
}
