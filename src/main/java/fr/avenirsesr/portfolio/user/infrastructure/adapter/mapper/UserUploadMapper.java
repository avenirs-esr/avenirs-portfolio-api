package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.UserUpload;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserUploadEntity;

public interface UserUploadMapper {
  static UserUploadEntity fromDomain(UserUpload userUpload) {
    return UserUploadEntity.of(
        userUpload.getId(),
        userUpload.getUserId(),
        userUpload.getType(),
        userUpload.getUrl(),
        userUpload.getSize(),
        userUpload.getMediaType());
  }

  static UserUpload toDomain(UserUploadEntity userUploadEntity) {
    return UserUpload.toDomain(
        userUploadEntity.getId(),
        userUploadEntity.getUserId(),
        userUploadEntity.getType(),
        userUploadEntity.getUrl(),
        userUploadEntity.getSize(),
        userUploadEntity.getMediaType());
  }
}
