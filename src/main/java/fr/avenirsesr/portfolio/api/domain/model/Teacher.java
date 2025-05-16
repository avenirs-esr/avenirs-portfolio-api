package fr.avenirsesr.portfolio.api.domain.model;

import jakarta.persistence.Lob;
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

    @Lob
    private byte[] profilePicture;

    @Lob
    private byte[] coverPicture;

    private Teacher(User user) {
        this.user = user;
    }

    public static Teacher create(User user) {
        return new Teacher(user);
    }

    public static Teacher toDomain(User user, String bio, byte[] profilePicture, byte[] coverPicture) {
        var teacher = new Teacher(user);
        teacher.setBio(bio);
        teacher.setProfilePicture(profilePicture);
        teacher.setCoverPicture(coverPicture);
        return teacher;
    }

    public UUID getId() {
        return user.getId();
    }
}
