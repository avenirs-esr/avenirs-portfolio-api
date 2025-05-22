package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileDTO;
import fr.avenirsesr.portfolio.api.domain.model.User;


public interface ProfileMapper {
    static ProfileDTO userStudentDomainToDto(User user) {
        return ProfileDTO.builder()
            .firstname(user.getFirstName())
            .lastname(user.getLastName())
            .bio(user.getStudent().getBio())
            .profilePicture(user.getStudent().getProfilePicture())
            .coverPicture(user.getStudent().getCoverPicture())
            .build();
    }

    static ProfileDTO userTeacherDomainToDto(User user) {
        return ProfileDTO.builder()
            .firstname(user.getFirstName())
            .lastname(user.getLastName())
            .bio(user.getTeacher().getBio())
            .profilePicture(user.getTeacher().getProfilePicture())
            .coverPicture(user.getTeacher().getCoverPicture())
            .build();
    }

}
