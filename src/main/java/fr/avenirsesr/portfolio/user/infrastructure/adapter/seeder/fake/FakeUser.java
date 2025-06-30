package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

public class FakeUser {
  private static final FakerProvider faker = new FakerProvider();
  private final UserEntity user;
  @Getter private Student student;
  @Getter private Teacher teacher;

  private FakeUser(UserEntity user) {
    this.user = user;
  }

  public static FakeUser create() {
    return new FakeUser(
        UserEntity.of(
            UUID.fromString(faker.call().internet().uuid()),
            faker.call().name().firstName(),
            faker.call().name().lastName(),
            null,
            null,
            null));
  }

  public FakeUser withEmail() {
    user.setEmail(faker.call().internet().emailAddress());
    return this;
  }

  public FakeUser withStudent() {
    user.setStudent(
        StudentEntity.of(
            faker.call().lorem().characters(50, 255, true),
            true,
            faker.call().company().url()
                + "/"
                + getFakePictureFilename(user, EUserCategory.STUDENT, "profile"),
            faker.call().company().url()
                + "/"
                + getFakePictureFilename(user, EUserCategory.STUDENT, "cover")));
    return this;
  }

  public FakeUser withTeacher() {
    user.setTeacher(
        TeacherEntity.of(
            faker.call().lorem().characters(50, 255, true),
            true,
            faker.call().company().url()
                + "/"
                + getFakePictureFilename(user, EUserCategory.TEACHER, "profile"),
            faker.call().company().url()
                + "/"
                + getFakePictureFilename(user, EUserCategory.TEACHER, "cover")));
    return this;
  }

  public UserEntity toEntity() {
    return user;
  }

  private String getFakePictureFilename(
      UserEntity user, EUserCategory userCategory, String prefixFileName) {
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
