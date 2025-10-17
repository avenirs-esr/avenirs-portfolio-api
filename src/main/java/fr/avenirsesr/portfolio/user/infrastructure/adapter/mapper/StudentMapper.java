package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;

public interface StudentMapper {
  static StudentEntity fromDomain(Student student) {
    return StudentEntity.of(UserMapper.fromDomain(student.getUser()), student.getBio());
  }

  static Student toDomain(StudentEntity studentEntity) {
    return Student.toDomain(
        UserMapper.toDomain(studentEntity.getUser()),
        studentEntity.getBio(),
        studentEntity.getCreatedAt(),
        studentEntity.getUpdatedAt());
  }
}
