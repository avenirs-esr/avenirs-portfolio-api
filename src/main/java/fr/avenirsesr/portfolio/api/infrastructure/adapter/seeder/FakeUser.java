package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import net.datafaker.Faker;

import java.time.Instant;

public class FakeUser {
  private static final Faker faker = new Faker();
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
    student.setProfilePicture(faker.company().url() + "/" + getFakePictureFilename(user, EUserCategory.STUDENT, "profile"));
    student.setCoverPicture(faker.company().url() + "/" + getFakePictureFilename(user, EUserCategory.STUDENT, "cover"));

    user.setStudent(student);
    return this;
  }

  public FakeUser withTeacher() {
    var teacher = Teacher.create(user);
    teacher.setBio(faker.lorem().paragraph());
    teacher.setProfilePicture(faker.company().url() + "/" + getFakePictureFilename(user, EUserCategory.TEACHER, "profile"));
    teacher.setCoverPicture(faker.company().url() + "/" + getFakePictureFilename(user, EUserCategory.TEACHER, "cover"));

    user.setTeacher(teacher);
    return this;
  }

  public User toModel() {
    return user;
  }

  private String getFakePictureFilename(User user, EUserCategory userCategory, String prefixFileName) {
    Instant instantNow = Instant.now();
    return user.getId()
            + "_"
            + userCategory.name()
            + "_"
            + instantNow.toEpochMilli()
            + "_"
            + prefixFileName
            + "_picture.jpg";
  }
}
