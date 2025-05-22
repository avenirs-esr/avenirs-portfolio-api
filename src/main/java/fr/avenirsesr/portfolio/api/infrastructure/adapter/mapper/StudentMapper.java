package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface StudentMapper {
  static StudentEntity fromDomain(Student student) {
    return new StudentEntity(
        student.getBio(),
        student.getUser().isStudent(),
        student.getProfilePicture(),
        student.getCoverPicture());
  }

  static Student toDomain(StudentEntity studentEntity, UserEntity userEntity) {
    return Student.toDomain(
        UserMapper.toDomain(userEntity),
        studentEntity.getBio(),
        studentEntity.getProfilePicture(),
        studentEntity.getCoverPicture());
  }
}
