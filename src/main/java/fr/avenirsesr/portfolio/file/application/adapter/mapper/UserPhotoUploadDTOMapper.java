package fr.avenirsesr.portfolio.file.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.dto.UserPhotoUploadDTO;
import fr.avenirsesr.portfolio.file.domain.model.UserPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPhotoUploadDTOMapper {

  @Mapping(source = "size", target = "fileSize")
  UserPhotoUploadDTO fromDomain(UserPhoto userPhoto);
}
