package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;

public interface StudentMapper {
  static StudentEntity fromDomain(Student student) {
    return StudentEntity.of(student.getBio(), student.getUser().isStudent());
  }

  static Student toDomain(StudentEntity studentEntity, UserEntity userEntity) {
    return Student.toDomain(UserMapper.toDomain(userEntity), studentEntity.getBio());
  }
}
