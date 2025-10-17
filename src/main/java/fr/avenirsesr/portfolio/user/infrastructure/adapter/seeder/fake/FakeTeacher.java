package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import fr.avenirsesr.portfolio.user.domain.port.output.seeder.TeacherDataGenerator;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public class FakeTeacher {
  private static final DataGeneratorProvider<TeacherDataGenerator> teacherDataGenerator =
      new DataGeneratorProvider<TeacherDataGenerator>()
          .init(FakeTeacher.class, TeacherDataGenerator.class);

  private final TeacherEntity teacher;

  private FakeTeacher(TeacherEntity teacher) {
    this.teacher = teacher;
  }

  public static FakeTeacher create(UserEntity user) {
    return new FakeTeacher(
        TeacherEntity.of(user, teacherDataGenerator.with("teacher-bio").teacherDescription()));
  }

  public FakeTeacher withBio(String bio) {
    teacher.setBio(bio);
    return this;
  }

  public TeacherEntity toEntity() {
    return teacher;
  }
}
