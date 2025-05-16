package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProfileDTO;
import fr.avenirsesr.portfolio.api.domain.model.User;


public interface ProfileMapper {
    static ProfileDTO userDomainToDto(User user) {
        return ProfileDTO.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .bio(user.getStudent().getBio())
                .profilePictureBase64(user.getStudent().getProfilePicture().toString())     // TODO : à remplacer par l'image en base64
                .coverPictureBase64(user.getStudent().getCoverPicture().toString())         // TODO : à remplacer par l'image en base64
                .build();
    }

    private static String imageBytestoStringBase64(byte[] imageBytes) {
        // String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return null;
        // String contentType = Files.probeContentType(path);
        // return "data:" + contentType + ";base64," + base64;
    }
}
