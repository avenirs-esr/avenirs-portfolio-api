package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public interface UserPhotoMapper {
  static UserPhotoEntity fromDomain(UserPhoto userPhoto) {
    return UserPhotoEntity.of(
        userPhoto.getId(),
        UserMapper.fromDomain(userPhoto.getUser()),
        userPhoto.getUserCategory(),
        userPhoto.getUserPhotoType(),
        userPhoto.getFileType(),
        userPhoto.getSize(),
        userPhoto.getVersion(),
        userPhoto.isActiveVersion(),
        userPhoto.getUri(),
        UserMapper.fromDomain(userPhoto.getUploadedBy()),
        userPhoto.getUploadedAt());
  }

  static UserPhoto toDomain(UserPhotoEntity entity) {
    return UserPhoto.toDomain(
        entity.getId(),
        entity.getFileType(),
        entity.getSize(),
        entity.getVersion(),
        entity.isActiveVersion(),
        entity.getUri(),
        UserMapper.toDomain(entity.getUploadedBy()),
        entity.getUploadedAt(),
        UserMapper.toDomain(entity.getUser()),
        entity.getUserCategory(),
        entity.getUserPhotoType());
  }
}
