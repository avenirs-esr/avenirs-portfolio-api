package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.Optional;

public interface StudentProgressMapper {
  static StudentProgressEntity fromDomain(StudentProgress studentProgress) {
    return new StudentProgressEntity(
        UserMapper.fromDomain(studentProgress.getUser()),
        TrainingPathMapper.fromDomain(studentProgress.getTrainingPath()),
        SkillLevelMapper.fromDomain(studentProgress.getSkillLevel()),
        studentProgress.getStatus());
  }

  static StudentProgress toDomain(StudentProgressEntity studentProgressEntity) {
    return StudentProgress.toDomain(
        studentProgressEntity.getId(),
        UserMapper.toDomain(studentProgressEntity.getStudent()),
        TrainingPathMapper.toDomain(studentProgressEntity.getTrainingPath()),
        SkillLevelMapper.toDomainWithoutRecursion(studentProgressEntity.getSkillLevel()),
        Optional.ofNullable(studentProgressEntity.getStatus()));
  }
}
