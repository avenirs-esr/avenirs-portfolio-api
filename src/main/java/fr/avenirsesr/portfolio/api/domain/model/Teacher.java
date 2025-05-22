package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TeacherEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Teacher {

    @Setter(AccessLevel.NONE)
    private final User user;
    private String bio;
    private String profilePicture;
    private String coverPicture;

    private Teacher(User user) {
        this.user = user;
    }

    public static Teacher create(User user) {
        return new Teacher(user);
    }

    public static Teacher toDomain(User user, String bio, String profilePicture, String coverPicture) {
        var teacher = new Teacher(user);
        teacher.setBio(bio);
        teacher.setProfilePicture(profilePicture);
        teacher.setCoverPicture(coverPicture);
        return teacher;
    }

    public UUID getId() {
        return user.getId();
    }

    public static Teacher entityToModel(User user, TeacherEntity teacherEntity) {
        var teacher = new Teacher(user);
        teacher.setBio(teacherEntity.getBio());
        teacher.setProfilePicture(teacherEntity.getProfilePicture());
        teacher.setCoverPicture(teacherEntity.getCoverPicture());
        return teacher;
    }
}
