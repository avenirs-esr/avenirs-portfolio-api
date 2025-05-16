package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserService {
    User getProfile(UUID id);

    void updateProfile(UUID id, String firstname, String lastname, String email, String bio);

    void updateProfilePicture(UUID id, MultipartFile photoFile) throws IOException;

    void updateCoverPicture(UUID id, MultipartFile coverFile) throws IOException;

}
