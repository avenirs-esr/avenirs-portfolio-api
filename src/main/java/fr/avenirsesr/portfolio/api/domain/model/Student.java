package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.StudentEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Student {

    @Setter(AccessLevel.NONE)
    private final User user;
    private String bio;
    private String profilePicture;
    private String coverPicture;

    private Student(User user) {
        this.user = user;
    }

    public static Student create(User user) {
        return new Student(user);
    }

    public static Student toDomain(User user, String bio, String profilePicture, String coverPicture) {
        var student = new Student(user);
        student.setBio(bio);
        student.setProfilePicture(profilePicture);
        student.setCoverPicture(coverPicture);
        return student;
    }

    public UUID getId() {
        return user.getId();
    }

    public static Student entityToModel(User user, StudentEntity studentEntity) {
        var student = new Student(user);
        student.setBio(studentEntity.getBio());
        student.setProfilePicture(studentEntity.getProfilePicture());
        student.setCoverPicture(studentEntity.getCoverPicture());
        return student;
    }
}
