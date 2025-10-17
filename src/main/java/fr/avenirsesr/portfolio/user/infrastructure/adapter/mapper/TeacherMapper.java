package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;

public interface TeacherMapper {
  static TeacherEntity fromDomain(Teacher teacher) {
    return TeacherEntity.of(UserMapper.fromDomain(teacher.getUser()), teacher.getBio());
  }

  static Teacher toDomain(TeacherEntity teacherEntity) {
    return Teacher.toDomain(
        UserMapper.toDomain(teacherEntity.getUser()),
        teacherEntity.getBio(),
        teacherEntity.getCreatedAt(),
        teacherEntity.getUpdatedAt());
  }
}
