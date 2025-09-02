package fr.avenirsesr.portfolio.user.application.adapter.mapper;

import fr.avenirsesr.portfolio.user.application.adapter.dto.ProfileOverviewDTO;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.domain.model.UserPhotos;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;

public interface ProfileOverviewMapper {
  static ProfileOverviewDTO userDomainToDto(
      User user, EUserCategory userCategory, UserPhotos userPhotos) {
    return new ProfileOverviewDTO(
        user.getFirstName(),
        user.getLastName(),
        switch (userCategory) {
          case STUDENT -> user.toStudent().getBio();
          case TEACHER -> user.toTeacher().getBio();
        },
        user.getEmail(),
        new ProfileOverviewDTO.PictureDTO(
            userPhotos.profileFileId().orElse(null), userPhotos.profileUrl()),
        new ProfileOverviewDTO.PictureDTO(
            userPhotos.coverFileId().orElse(null), userPhotos.coverUrl()));
  }
}
