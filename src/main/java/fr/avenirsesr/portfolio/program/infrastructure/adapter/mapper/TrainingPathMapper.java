package fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import java.util.Set;
import java.util.stream.Collectors;

public class TrainingPathMapper implements Mapper<TrainingPathEntity, TrainingPath> {
  public static TrainingPathMapper INSTANCE = new TrainingPathMapper();

  @Override
  public TrainingPathEntity fromDomain(TrainingPath trainingPath) {
    var entity =
        new TrainingPathEntity(
            trainingPath.getId(),
            ProgramMapper.INSTANCE.fromDomain(trainingPath.getProgram()),
            Set.of());

    entity.setSkillLevels(
        trainingPath.getSkillLevels().stream()
            .map(SkillLevelMapper.INSTANCE::fromDomain)
            .collect(Collectors.toSet()));

    return entity;
  }

  @Override
  public TrainingPath toDomain(TrainingPathEntity trainingPathEntity) {
    var trainingPath =
        TrainingPath.toDomain(
            trainingPathEntity.getId(),
            ProgramMapper.INSTANCE.toDomain(trainingPathEntity.getProgram()),
            Set.of(),
            trainingPathEntity.getCreatedAt(),
            trainingPathEntity.getUpdatedAt());

    trainingPath.setSkillLevels(
        trainingPathEntity.getSkillLevels().stream()
            .map(SkillLevelMapper.INSTANCE::toDomain)
            .collect(Collectors.toSet()));

    return trainingPath;
  }

  @Override
  public TrainingPath toDomain(TrainingPathEntity trainingPathEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return TrainingPath.toDomain(
        trainingPathEntity.getId(),
        attributes.contains("program")
            ? ProgramMapper.INSTANCE.toDomain(
                trainingPathEntity.getProgram(), graph.from("program"))
            : null,
        attributes.contains("skillLevels")
            ? trainingPathEntity.getSkillLevels().stream()
                .map(
                    entity -> SkillLevelMapper.INSTANCE.toDomain(entity, graph.from("skillLevels")))
                .collect(Collectors.toSet())
            : Set.of(),
        trainingPathEntity.getCreatedAt(),
        trainingPathEntity.getUpdatedAt());
  }
}
