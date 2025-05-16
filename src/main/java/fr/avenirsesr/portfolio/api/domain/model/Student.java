package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.UUID;

@Getter
@Setter
public class Student {

    @Setter(AccessLevel.NONE)
    private final User user;

    private String bio;

    @Lob
    private byte[] profilePicture;

    @Lob
    private byte[] coverPicture;

    private Student(User user) {
        this.user = user;
    }

    public static Student create(User user) {
        return new Student(user);
    }

    public static Student toDomain(User user, String bio, byte[] profilePicture, byte[] coverPicture) {
        var student = new Student(user);
        student.setBio(bio);
        student.setProfilePicture(profilePicture);
        student.setCoverPicture(coverPicture);
        return student;
    }

    public UUID getId() {
        return user.getId();
    }

    private StudentEntity modelToEntity(Student student) {
        return new StudentEntity(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                studentModelToEntity(user.getStudent()),
                teacherModelToEntity(user.getTeacher())
        );
    }

    private Student entityToModel(StudentEntity studentEntity) {
        return Student.toDomain(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                studentEntityToModel(userEntity.getStudent()),
                teacherEntityToModel(userEntity.getTeacher())
        );
    }
}
