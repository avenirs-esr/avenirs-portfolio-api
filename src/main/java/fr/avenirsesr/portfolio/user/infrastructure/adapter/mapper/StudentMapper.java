package fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import java.util.List;

public class StudentMapper implements Mapper<StudentEntity, Student> {
  public static final StudentMapper INSTANCE = new StudentMapper();

  @Override
  public StudentEntity fromDomain(Student student) {
    return StudentEntity.of(
        UserMapper.INSTANCE.fromDomain(student.getUser()),
        student.getInstitutionEmail(),
        student.getInstitutionId(),
        student.getGroupId(),
        student.getBio(),
        student.isHasUnseenNotification(),
        student.getSelfKnowledgeCategories(),
        student.getCoverPicture().map(FileMapper.INSTANCE::fromDomain).orElse(null),
        student.getProfilePicture().map(FileMapper.INSTANCE::fromDomain).orElse(null),
        student.getCreatedAt(),
        student.getUpdatedAt());
  }

  @Override
  public Student toDomain(StudentEntity studentEntity) {
    return Student.toDomain(
        UserMapper.INSTANCE.toDomain(studentEntity.getUser()),
        studentEntity.getInstitutionEmail(),
        studentEntity.getInstitutionId(),
        studentEntity.getGroupId(),
        studentEntity.getBio(),
        studentEntity.isHasUnseenNotification(),
        studentEntity.getSelfKnowledgeCategories() == null
            ? List.of()
            : studentEntity.getSelfKnowledgeCategories(),
        studentEntity.getCoverPicture() == null
            ? null
            : FileMapper.INSTANCE.toDomain(studentEntity.getCoverPicture()),
        studentEntity.getProfilePicture() == null
            ? null
            : FileMapper.INSTANCE.toDomain(studentEntity.getProfilePicture()),
        studentEntity.getCreatedAt(),
        studentEntity.getUpdatedAt());
  }

  @Override
  public Student toDomain(StudentEntity studentEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return Student.toDomain(
        attributes.contains("user") ? UserMapper.INSTANCE.toDomain(studentEntity.getUser()) : null,
        studentEntity.getInstitutionEmail(),
        studentEntity.getInstitutionId(),
        studentEntity.getGroupId(),
        studentEntity.getBio(),
        studentEntity.isHasUnseenNotification(),
        studentEntity.getSelfKnowledgeCategories() == null
            ? List.of()
            : studentEntity.getSelfKnowledgeCategories(),
        studentEntity.getCoverPicture() == null || !attributes.contains("coverPicture")
            ? null
            : FileMapper.INSTANCE.toDomain(studentEntity.getCoverPicture()),
        studentEntity.getProfilePicture() == null || !attributes.contains("profilePicture")
            ? null
            : FileMapper.INSTANCE.toDomain(studentEntity.getProfilePicture()),
        studentEntity.getCreatedAt(),
        studentEntity.getUpdatedAt());
  }
}
