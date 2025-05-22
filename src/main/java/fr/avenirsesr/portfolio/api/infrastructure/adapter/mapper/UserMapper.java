package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.Teacher;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface UserMapper {
  static UserEntity fromDomain(User user) {
    return new UserEntity(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        StudentMapper.fromDomain(user.toStudent()),
        TeacherMapper.fromDomain(user.toTeacher()));
  }

  static UserEntity fromDomain(Student student) {
    return new UserEntity(
        student.getUser().getId(),
        student.getUser().getFirstName(),
        student.getUser().getLastName(),
        student.getUser().getEmail(),
        StudentMapper.fromDomain(student),
        TeacherMapper.fromDomain(student.getUser().toTeacher()));
  }

  static UserEntity fromDomain(Teacher teacher) {
    return new UserEntity(
        teacher.getUser().getId(),
        teacher.getUser().getFirstName(),
        teacher.getUser().getLastName(),
        teacher.getUser().getEmail(),
        StudentMapper.fromDomain(teacher.getUser().toStudent()),
        TeacherMapper.fromDomain(teacher));
  }

  static User toDomain(UserEntity userEntity) {
    return User.toDomain(
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
        userEntity.getTeacher().getCoverPicture());
  }
}
