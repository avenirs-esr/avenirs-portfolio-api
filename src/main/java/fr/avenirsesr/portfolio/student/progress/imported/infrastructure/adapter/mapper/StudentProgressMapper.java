package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import java.util.List;

public class StudentProgressMapper implements Mapper<StudentProgressEntity, StudentProgress> {
  public static final StudentProgressMapper INSTANCE = new StudentProgressMapper();

  @Override
  public StudentProgressEntity fromDomain(StudentProgress studentProgress) {
    return new StudentProgressEntity(
        StudentMapper.INSTANCE.fromDomain(studentProgress.getStudent()),
        TrainingPathMapper.INSTANCE.fromDomain(studentProgress.getTrainingPath()),
        studentProgress.getAllSkillLevels().stream()
            .map(SkillLevelProgressMapper.INSTANCE::fromDomain)
            .toList());
  }

  @Override
  public StudentProgress toDomain(StudentProgressEntity studentProgressEntity) {
    return StudentProgress.toDomain(
        studentProgressEntity.getId(),
        StudentMapper.INSTANCE.toDomain(studentProgressEntity.getStudent()),
        TrainingPathMapper.INSTANCE.toDomain(studentProgressEntity.getTrainingPath()),
        studentProgressEntity.getStartDate(),
        studentProgressEntity.getEndDate(),
        studentProgressEntity.getSkillLevels().stream()
            .map(SkillLevelProgressMapper.INSTANCE::toDomain)
            .toList(),
        studentProgressEntity.getCreatedAt(),
        studentProgressEntity.getUpdatedAt());
  }

  @Override
  public StudentProgress toDomain(
      StudentProgressEntity studentProgressEntity, EntityGrapher<?> graph) {
    var attributs = graph.attributes();

    return StudentProgress.toDomain(
        studentProgressEntity.getId(),
        attributs.contains("student")
            ? StudentMapper.INSTANCE.toDomain(
                studentProgressEntity.getStudent(), graph.from("student"))
            : null,
        attributs.contains("trainingPath")
            ? TrainingPathMapper.INSTANCE.toDomain(
                studentProgressEntity.getTrainingPath(), graph.from("trainingPath"))
            : null,
        studentProgressEntity.getStartDate(),
        studentProgressEntity.getEndDate(),
        attributs.contains("skillLevels")
            ? studentProgressEntity.getSkillLevels().stream()
                .map(
                    entity ->
                        SkillLevelProgressMapper.INSTANCE.toDomain(
                            entity, graph.from("skillLevels")))
                .toList()
            : List.of(),
        studentProgressEntity.getCreatedAt(),
        studentProgressEntity.getUpdatedAt());
  }
}
