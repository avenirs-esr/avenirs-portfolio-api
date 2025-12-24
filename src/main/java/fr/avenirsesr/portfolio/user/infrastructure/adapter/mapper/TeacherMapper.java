package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;

public class TeacherMapper implements Mapper<TeacherEntity, Teacher> {
  public static final TeacherMapper INSTANCE = new TeacherMapper();

  @Override
  public TeacherEntity fromDomain(Teacher teacher) {
    return TeacherEntity.of(UserMapper.INSTANCE.fromDomain(teacher.getUser()), teacher.getBio());
  }

  @Override
  public Teacher toDomain(TeacherEntity teacherEntity) {
    return Teacher.toDomain(
        UserMapper.INSTANCE.toDomain(teacherEntity.getUser()),
        teacherEntity.getBio(),
        teacherEntity.getCreatedAt(),
        teacherEntity.getUpdatedAt());
  }
}
