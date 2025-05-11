package fr.avenirsesr.portfolio.api.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {
    @Setter(AccessLevel.NONE)
    private final UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Student student;
    private Teacher teacher;

    private User(UUID id) {
        this.id = id;
    }

    public static User create(String firstName, String lastName) {
        var user = new User(UUID.randomUUID());
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }

    public static User toDomain(UUID id, String firstName, String lastName, String email, Student student, Teacher teacher) {
        var user = new User(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setStudent(student);
        user.setTeacher(teacher);

        return user;
    }

}
