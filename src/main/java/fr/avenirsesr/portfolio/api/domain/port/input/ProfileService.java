package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProfileService {
    User getProfile(UUID id);

    User updateProfile(UUID id, String firstname, String lastname, String email, String bio);

    User updateProfilePhoto(UUID id, MultipartFile photoFile) throws IOException;

    User updateProfileCover(UUID id, MultipartFile coverFile) throws IOException;

}
