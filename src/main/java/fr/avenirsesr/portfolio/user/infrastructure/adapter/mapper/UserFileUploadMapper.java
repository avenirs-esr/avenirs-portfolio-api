package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.UserFileUpload;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserFileUploadEntity;

public interface UserFileUploadMapper {
  static UserFileUploadEntity fromDomain(UserFileUpload userFileUpload) {
    return UserFileUploadEntity.of(
        userFileUpload.getId(),
        UserMapper.fromDomain(userFileUpload.getUser()),
        userFileUpload.getType(),
        userFileUpload.getUrl(),
        userFileUpload.getSize(),
        userFileUpload.getMediaType());
  }

  static UserFileUpload toDomain(UserFileUploadEntity userFileUploadEntity) {
    return UserFileUpload.toDomain(
        userFileUploadEntity.getId(),
        UserMapper.toDomain(userFileUploadEntity.getUser()),
        userFileUploadEntity.getType(),
        userFileUploadEntity.getUrl(),
        userFileUploadEntity.getSize(),
        userFileUploadEntity.getMediaType());
  }
}
