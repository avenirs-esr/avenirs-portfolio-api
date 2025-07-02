package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.Teacher;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public interface UserMapper {
  static UserEntity fromDomain(User user) {
    return user != null
        ? new UserEntity(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            StudentMapper.fromDomain(user.toStudent()),
            TeacherMapper.fromDomain(user.toTeacher()))
        : null;
  }

  static UserEntity fromDomain(Student student) {
    return student != null
        ? new UserEntity(
            student.getUser().getId(),
            student.getUser().getFirstName(),
            student.getUser().getLastName(),
            student.getUser().getEmail(),
            StudentMapper.fromDomain(student),
            TeacherMapper.fromDomain(student.getUser().toTeacher()))
        : null;
  }

  static UserEntity fromDomain(Teacher teacher) {
    return teacher != null
        ? new UserEntity(
            teacher.getUser().getId(),
            teacher.getUser().getFirstName(),
            teacher.getUser().getLastName(),
            teacher.getUser().getEmail(),
            StudentMapper.fromDomain(teacher.getUser().toStudent()),
            TeacherMapper.fromDomain(teacher))
        : null;
  }

  static User toDomain(UserEntity userEntity) {
    return userEntity != null
        ? User.toDomain(
            userEntity.getId(),
            userEntity.getFirstName(),
            userEntity.getLastName(),
            userEntity.getEmail(),
            userEntity.getStudent().isActive(),
            userEntity.getStudent().getBio(),
            userEntity.getStudent().getProfilePicture(),
            userEntity.getStudent().getCoverPicture(),
            userEntity.getTeacher().isActive(),
            userEntity.getTeacher().getBio(),
            userEntity.getTeacher().getProfilePicture(),
            userEntity.getTeacher().getCoverPicture())
        : null;
  }
}
