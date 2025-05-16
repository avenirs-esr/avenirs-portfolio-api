package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.ProfileService;
import fr.avenirsesr.portfolio.api.domain.port.output.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getProfile(UUID id) {
        return Optional.of(userRepository.findById(id))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Override
    public User updateProfile(UUID id, String firstname, String lastname, String email, String bio) {
        User user = Optional.of(userRepository.findById(id))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

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

        return userRepository.save(user);
    }

    @Override
    public User updateProfilePhoto(UUID id, MultipartFile photoFile) throws IOException {
        User user = Optional.of(userRepository.findById(id))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Student userStudent = user.getStudent();
        userStudent.setProfilePicture(photoFile.getBytes());
        user.setStudent(userStudent);

        return userRepository.save(user);
    }

    @Override
    public User updateProfileCover(UUID id, MultipartFile coverFile) throws IOException {
        User user = Optional.of(userRepository.findById(id))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Student userStudent = user.getStudent();
        userStudent.setCoverPicture(coverFile.getBytes());
        user.setStudent(userStudent);

        return userRepository.save(user);
    }
}
