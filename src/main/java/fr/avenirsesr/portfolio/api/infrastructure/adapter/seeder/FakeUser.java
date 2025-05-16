package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import net.datafaker.Faker;

import java.net.URI;

public class FakeUser {
    private final static Faker faker = new Faker();
    private final User user;

    private FakeUser(User user) {
        this.user = user;
    }

    public static FakeUser create() {
        return new FakeUser(User.create(faker.name().firstName(), faker.name().lastName()));
    }

    public FakeUser withEmail() {
        user.setEmail(faker.internet().emailAddress());
        return this;
    }

    public FakeUser withStudent() {
        var student = Student.create(user);
        student.setBio(faker.lorem().paragraph());
        student.setProfilePicture(faker.internet().image().getBytes());
        student.setCoverPicture(faker.internet().image().getBytes());

        user.setStudent(student);
        return this;
    }

    public FakeUser withTeacher() {
        var teacher = Teacher.create(user);
        teacher.setBio(faker.lorem().paragraph());
        teacher.setProfilePicture(faker.internet().image().getBytes());
        teacher.setCoverPicture(faker.internet().image().getBytes());

        user.setTeacher(teacher);
        return this;
    }

    public User toModel() {
        return user;
    }
}
