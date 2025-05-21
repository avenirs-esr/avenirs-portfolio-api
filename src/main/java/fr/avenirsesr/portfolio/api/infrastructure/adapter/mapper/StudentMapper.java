package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.StudentEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.UserEntity;

public interface StudentMapper {
  static StudentEntity fromModelToEntity(StudentEntity studentEntity) {
    return new StudentEntity();
  }

  static Student fromEntityToModel(UserEntity studentEntity) {
    return Student.toDomain(
        UserMapper.fromEntityToModel(studentEntity),
        studentEntity.getStudent().getBio(),
        studentEntity.getStudent().getProfilePicture(),
        studentEntity.getStudent().getCoverPicture());
  }
}
