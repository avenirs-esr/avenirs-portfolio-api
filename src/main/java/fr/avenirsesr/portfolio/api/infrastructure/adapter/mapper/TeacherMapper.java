package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface TeacherMapper {
  static TeacherEntity fromModelToEntity(TeacherEntity TeacherEntity) {
    return new TeacherEntity();
  }

  static Teacher fromEntityToModel(UserEntity teacherEntity) {
    return Teacher.toDomain(
        UserMapper.fromEntityToModel(teacherEntity),
        teacherEntity.getTeacher().getBio(),
        teacherEntity.getTeacher().getProfilePicture(),
        teacherEntity.getTeacher().getCoverPicture());
  }
}
