package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface UserMapper {
  static UserEntity fromDomain(User user) {
    return new UserEntity(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        StudentMapper.fromDomain(user.toStudent()),
        TeacherMapper.fromDomain(user.toTeacher()));
  }

  static User toDomain(UserEntity userEntity) {
    return User.toDomain(
        userEntity.getId(),
        userEntity.getFirstName(),
        userEntity.getLastName(),
        userEntity.getEmail(),
        userEntity.getStudent().getBio(),
        userEntity.getStudent().getProfilePicture(),
        userEntity.getStudent().getCoverPicture(),
        userEntity.getTeacher().getBio(),
        userEntity.getTeacher().getProfilePicture(),
        userEntity.getTeacher().getCoverPicture());
  }
}
