package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.StudentDataGenerator;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.UserDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public class FakeStudent {
  private static final DataGeneratorProvider<StudentDataGenerator> studentDataGenerator =
      new DataGeneratorProvider<StudentDataGenerator>()
          .init(FakeStudent.class, StudentDataGenerator.class);
  private static final DataGeneratorProvider<UserDataGenerator> userDataGenerator =
      new DataGeneratorProvider<UserDataGenerator>()
          .init(FakeStudent.class, UserDataGenerator.class);
  private final StudentEntity student;

  private FakeStudent(StudentEntity student) {
    this.student = student;
  }

  public static FakeStudent create(UserEntity user) {
    return new FakeStudent(
        StudentEntity.of(
            user,
            userDataGenerator.with("student-email").email(),
            studentDataGenerator.with("student-bio").studentDescription()));
  }

  public FakeStudent withBio(String bio) {
    student.setBio(bio);
    return this;
  }

  public StudentEntity toEntity() {
    return student;
  }
}
