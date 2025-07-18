package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.dto.StudentTrainingPathSummaryDTO;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import java.util.Set;
import java.util.stream.Collectors;

public interface TrainingPathMapper {
  static TrainingPathEntity fromDomain(TrainingPath trainingPath) {
    var entity =
        new TrainingPathEntity(
            trainingPath.getId(), ProgramMapper.fromDomain(trainingPath.getProgram()), Set.of());

    entity.setSkillLevels(
        trainingPath.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel, SkillMapper.fromDomain(skillLevel.getSkill())))
            .collect(Collectors.toSet()));

    return entity;
  }

  static TrainingPath toDomain(TrainingPathEntity trainingPathEntity) {
    var trainingPath =
        TrainingPath.toDomain(
            trainingPathEntity.getId(),
            ProgramMapper.toDomain(trainingPathEntity.getProgram()),
            Set.of());

    trainingPath.setSkillLevels(
        trainingPathEntity.getSkillLevels().stream()
            .map(SkillLevelMapper::toDomain)
            .collect(Collectors.toSet()));

    return trainingPath;
  }

  static TrainingPath toDomainWithoutRecursion(
      StudentTrainingPathSummaryDTO studentTrainingPathSummaryDTO) {
    return TrainingPath.toDomain(
        studentTrainingPathSummaryDTO.id(),
        ProgramMapper.toDomain(studentTrainingPathSummaryDTO.program()),
        Set.of());
  }
}
