package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public interface StudentProgressMapper {
  static StudentProgressEntity fromDomain(StudentProgress studentProgress) {
    return new StudentProgressEntity(
        StudentMapper.fromDomain(studentProgress.getStudent()),
        TrainingPathMapper.fromDomain(studentProgress.getTrainingPath()),
        studentProgress.getAllSkillLevels().stream()
            .map(SkillLevelProgressMapper::fromDomain)
            .toList());
  }

  static StudentProgress toDomain(StudentProgressEntity studentProgressEntity) {
    return StudentProgress.toDomain(
        studentProgressEntity.getId(),
        StudentMapper.toDomain(studentProgressEntity.getStudent()),
        TrainingPathMapper.toDomain(studentProgressEntity.getTrainingPath()),
        studentProgressEntity.getStartDate(),
        studentProgressEntity.getEndDate(),
        studentProgressEntity.getSkillLevels().stream()
            .map(SkillLevelProgressMapper::toDomain)
            .toList(),
        studentProgressEntity.getCreatedAt(),
        studentProgressEntity.getUpdatedAt());
  }
}
