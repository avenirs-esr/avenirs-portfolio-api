package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.FileDTO;
import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.domain.data.UserPhotosData;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileOverviewMapper {

  default ProfileOverviewDTO userDomainToDto(
      UserProfileOverviewData overview, UserPhotosData userPhotos) {
    return new ProfileOverviewDTO(
        overview.firstName(),
        overview.lastName(),
        overview.bio(),
        overview.email(),
        new FileDTO(
            userPhotos.profileFileId().orElse(null),
            userPhotos.profileFileName().orElse(null),
            userPhotos.profileFileUrl()),
        new FileDTO(
            userPhotos.coverFileId().orElse(null),
            userPhotos.coverFileName().orElse(null),
            userPhotos.coverFileUrl()));
  }
}
