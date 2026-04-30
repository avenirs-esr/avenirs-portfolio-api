package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.UserPhotoEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public class UserPhotoMapper implements Mapper<UserPhotoEntity, UserPhoto> {
  public static UserPhotoMapper INSTANCE = new UserPhotoMapper();

  @Override
  public UserPhotoEntity fromDomain(UserPhoto userPhoto) {
    return UserPhotoEntity.of(
        userPhoto.getId(),
        userPhoto.getName(),
        UserMapper.INSTANCE.fromDomain(userPhoto.getUser()),
        userPhoto.getUserCategory(),
        userPhoto.getUserPhotoType(),
        userPhoto.getFileType(),
        userPhoto.getSize(),
        userPhoto.getVersion(),
        userPhoto.isActiveVersion(),
        userPhoto.getUri(),
        UserMapper.INSTANCE.fromDomain(userPhoto.getUploadedBy()),
        userPhoto.getUploadedAt(),
        userPhoto.getCreatedAt(),
        userPhoto.getUpdatedAt());
  }

  @Override
  public UserPhoto toDomain(UserPhotoEntity entity) {
    return UserPhoto.toDomain(
        entity.getId(),
        entity.getName(),
        entity.getFileType(),
        entity.getSize(),
        entity.getVersion(),
        entity.isActiveVersion(),
        entity.getUri(),
        UserMapper.INSTANCE.toDomain(entity.getUploadedBy()),
        entity.getUploadedAt(),
        UserMapper.INSTANCE.toDomain(entity.getUser()),
        entity.getUserCategory(),
        entity.getUserPhotoType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
