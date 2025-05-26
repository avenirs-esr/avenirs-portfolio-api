package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileDTO;
import fr.avenirsesr.portfolio.api.domain.model.User;

public interface ProfileMapper {
  static ProfileDTO userStudentDomainToDto(User user) {
    return new ProfileDTO(
        user.getFirstName(),
        user.getLastName(),
        user.getStudentBio(),
        user.getStudentProfilePicture(),
        user.getStudentCoverPicture());
  }

  static ProfileDTO userTeacherDomainToDto(User user) {
    return new ProfileDTO(
        user.getFirstName(),
        user.getLastName(),
        user.getStudentBio(),
        user.getTeacherProfilePicture(),
        user.getTeacherCoverPicture());
  }
}
