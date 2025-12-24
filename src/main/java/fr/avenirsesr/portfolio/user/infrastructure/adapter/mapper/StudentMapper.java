package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;

public class StudentMapper implements Mapper<StudentEntity, Student> {
  public static final StudentMapper INSTANCE = new StudentMapper();

  @Override
  public StudentEntity fromDomain(Student student) {
    return StudentEntity.of(UserMapper.INSTANCE.fromDomain(student.getUser()), student.getBio());
  }

  @Override
  public Student toDomain(StudentEntity studentEntity) {
    return Student.toDomain(
        UserMapper.INSTANCE.toDomain(studentEntity.getUser()),
        studentEntity.getBio(),
        studentEntity.getCreatedAt(),
        studentEntity.getUpdatedAt());
  }

  @Override
  public Student toDomain(StudentEntity studentEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return Student.toDomain(
        attributes.contains("user") ? UserMapper.INSTANCE.toDomain(studentEntity.getUser()) : null,
        studentEntity.getBio(),
        studentEntity.getCreatedAt(),
        studentEntity.getUpdatedAt());
  }
}
