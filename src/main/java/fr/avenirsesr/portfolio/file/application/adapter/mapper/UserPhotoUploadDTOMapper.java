package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.UserPhotoUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;

public interface UserPhotoUploadDTOMapper {
  static UserPhotoUploadDTO fromDomain(UserPhoto userPhoto) {
    return new UserPhotoUploadDTO(
        userPhoto.getId(), userPhoto.getFileType(), userPhoto.getSize(), userPhoto.getVersion());
  }
}
