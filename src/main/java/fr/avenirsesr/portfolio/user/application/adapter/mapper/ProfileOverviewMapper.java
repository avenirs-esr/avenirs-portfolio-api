package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.domain.data.UserPhotosData;
import fr.avenirsesr.portfolio.user.domain.data.UserProfileOverviewData;

public interface ProfileOverviewMapper {
  static ProfileOverviewDTO userDomainToDto(
      UserProfileOverviewData overview, UserPhotosData userPhotos) {
    return new ProfileOverviewDTO(
        overview.firstName(),
        overview.lastName(),
        overview.bio(),
        overview.email(),
        new ProfileOverviewDTO.PictureDTO(
            userPhotos.profileFileId().orElse(null),
            userPhotos.profileFileName().orElse(null),
            userPhotos.profileFileUrl()),
        new ProfileOverviewDTO.PictureDTO(
            userPhotos.coverFileId().orElse(null),
            userPhotos.coverFileName().orElse(null),
            userPhotos.coverFileUrl()));
  }
}
