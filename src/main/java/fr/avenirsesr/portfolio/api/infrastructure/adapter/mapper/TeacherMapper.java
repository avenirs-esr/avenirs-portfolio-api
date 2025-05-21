package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface TeacherMapper {
  static TeacherEntity fromDomain(Teacher teacher) {
    return new TeacherEntity(
        teacher.getBio(), teacher.getProfilePicture(), teacher.getCoverPicture());
  }

  static Teacher toDomain(TeacherEntity teacherEntity, UserEntity userEntity) {
    return Teacher.toDomain(
        UserMapper.toDomain(userEntity),
        teacherEntity.getBio(),
        teacherEntity.getProfilePicture(),
        teacherEntity.getCoverPicture());
  }
}
