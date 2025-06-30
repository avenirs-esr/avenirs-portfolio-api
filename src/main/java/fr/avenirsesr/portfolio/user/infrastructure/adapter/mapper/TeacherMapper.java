package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.TeacherEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public interface TeacherMapper {
  static TeacherEntity fromDomain(Teacher teacher) {
    return TeacherEntity.of(
        teacher.getBio(),
        teacher.getUser().isTeacher(),
        teacher.getProfilePicture(),
        teacher.getCoverPicture());
  }

  static Teacher toDomain(TeacherEntity teacherEntity, UserEntity userEntity) {
    return Teacher.toDomain(
        UserMapper.toDomain(userEntity),
        teacherEntity.getBio(),
        teacherEntity.getProfilePicture(),
        teacherEntity.getCoverPicture());
  }
}
