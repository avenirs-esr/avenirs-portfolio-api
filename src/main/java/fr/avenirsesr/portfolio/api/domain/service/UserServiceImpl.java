package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.exception.BadImageSizeException;
import fr.avenirsesr.portfolio.api.domain.exception.BadImageTypeException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.UserService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class UserServiceImpl implements UserService {

    public static final long MAX_SIZE = 10 * 1024 * 1024;   // 10 Mo

    private final UserRepository userRepository;
    private final RessourceRepository ressourceRepository;

    public UserServiceImpl(UserRepository userRepository, RessourceRepository ressourceRepository) {
        this.userRepository = userRepository;
        this.ressourceRepository = ressourceRepository;
    }

    @Override
    public User getProfile(UUID id) {
        return getUser(id);
    }

    @Override
    public void updateProfile(UUID id, String firstname, String lastname, String email, String bio) {
        User user = getUser(id);

        if (firstname != null) {
            user.setFirstName(firstname);
        }

        if (lastname != null) {
            user.setLastName(lastname);
        }

        if (email != null) {
            user.setEmail(email);
        }

        Student userStudent = user.getStudent();
        userStudent.setBio(bio);
        user.setStudent(userStudent);

        userRepository.save(user);
    }

    @Override
    public void updateProfilePicture(UUID id, MultipartFile photoFile) throws IOException {
        User user = getUser(id);

        checkImageFormat(photoFile);

        String profilePicturePath = ressourceRepository.storeStudentProfilePicture(id, photoFile);

        Student userStudent = user.getStudent();
        userStudent.setProfilePicture(profilePicturePath);
        user.setStudent(userStudent);

        userRepository.save(user);
    }

    @Override
    public void updateCoverPicture(UUID id, MultipartFile coverFile) throws IOException {
        User user = getUser(id);

        checkImageFormat(coverFile);

        String coverPicturePath = ressourceRepository.storeStudentCoverPicture(id, coverFile);

        Student userStudent = user.getStudent();
        userStudent.setCoverPicture(coverPicturePath);
        user.setStudent(userStudent);

        userRepository.save(user);
    }

    private User getUser(UUID id) {
        return Optional.of(userRepository.findById(id))
            .orElseThrow(UserNotFoundException::new);
    }

    private void checkImageFormat(MultipartFile file) {
        String contentType = file.getContentType();
        long contentSize = file.getSize();

        if (!contentType.equals(MediaType.IMAGE_JPEG_VALUE) && !contentType.equals(MediaType.IMAGE_PNG_VALUE)) {
            throw new BadImageTypeException();
        }

        if (contentSize > MAX_SIZE) {
            throw new BadImageSizeException();
        }
    }
}
