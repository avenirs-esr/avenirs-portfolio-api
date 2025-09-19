package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import lombok.Getter;

public class FakeUser {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(FakeUser.class, SharedDataGenerator.class);

  private static final DataGeneratorProvider<UserDataGenerator> userDataGenerator =
      new DataGeneratorProvider<UserDataGenerator>().init(FakeUser.class, UserDataGenerator.class);
  private final UserEntity user;
  @Getter private Student student;
  @Getter private Teacher teacher;

  private FakeUser(UserEntity user) {
    this.user = user;
  }

  public static FakeUser create() {
    return new FakeUser(
        UserEntity.of(
            dataGenerator.with("id").uuid(),
            userDataGenerator.with("firstName").firstName(),
            userDataGenerator.with("lastName").lastName(),
            null,
            null,
            null));
  }

  public FakeUser withEmail() {
    user.setEmail(userDataGenerator.with("email").email());
    return this;
  }

  public FakeUser withStudent() {
    user.setStudent(
        StudentEntity.of(userDataGenerator.with("student-bio").studentDescription(), true));
    return this;
  }

  public FakeUser withTeacher() {
    user.setTeacher(
        TeacherEntity.of(userDataGenerator.with("teacher-bio").teacherDescription(), true));
    return this;
  }

  public UserEntity toEntity() {
    return user;
  }
}
