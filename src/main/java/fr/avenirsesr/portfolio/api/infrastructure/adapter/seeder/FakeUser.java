package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.EUserCategory;
import java.time.Instant;
import lombok.Getter;
import net.datafaker.Faker;

public class FakeUser {
  private static final Faker faker = new Faker();
  private final User user;
  @Getter private Student student;
  @Getter private Teacher teacher;

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
    var student = user.toStudent();
    student.setBio(faker.lorem().paragraph());
    student.setProfilePicture(
        faker.company().url()
            + "/"
            + getFakePictureFilename(user, EUserCategory.STUDENT, "profile"));
    student.setCoverPicture(
        faker.company().url() + "/" + getFakePictureFilename(user, EUserCategory.STUDENT, "cover"));

    this.student = student;
    user.setStudent(true);
    return this;
  }

  public FakeUser withTeacher() {
    var teacher = user.toTeacher();
    teacher.setBio(faker.lorem().paragraph());
    teacher.setProfilePicture(
        faker.company().url()
            + "/"
            + getFakePictureFilename(user, EUserCategory.TEACHER, "profile"));
    teacher.setCoverPicture(
        faker.company().url() + "/" + getFakePictureFilename(user, EUserCategory.TEACHER, "cover"));

    this.teacher = teacher;
    user.setTeacher(true);
    return this;
  }

  public User toModel() {
    return user;
  }

  private String getFakePictureFilename(
      User user, EUserCategory userCategory, String prefixFileName) {
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
