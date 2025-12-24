package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.SkillLevelProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;
import java.util.List;
import java.util.stream.Collectors;

public class SkillLevelProgressMapper
    implements Mapper<SkillLevelProgressEntity, SkillLevelProgress> {
  public static final SkillLevelProgressMapper INSTANCE = new SkillLevelProgressMapper();

  @Override
  public SkillLevelProgressEntity fromDomain(SkillLevelProgress skillLevelProgress) {
    return SkillLevelProgressEntity.of(
        skillLevelProgress.getId(),
        StudentMapper.INSTANCE.fromDomain(skillLevelProgress.getStudent()),
        SkillLevelMapper.INSTANCE.fromDomain(skillLevelProgress.getSkillLevel()),
        skillLevelProgress.getStatus(),
        skillLevelProgress.getStartDate(),
        skillLevelProgress.getEndDate(),
        skillLevelProgress.getAmses().stream()
            .map(AMSMapper.INSTANCE::fromDomain)
            .collect(Collectors.toSet()));
  }

  @Override
  public SkillLevelProgress toDomain(SkillLevelProgressEntity entity) {
    return SkillLevelProgress.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        SkillLevelMapper.INSTANCE.toDomain(entity.getSkillLevel()),
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getAmses().stream().map(AMSMapper.INSTANCE::toDomain).toList(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public SkillLevelProgress toDomain(SkillLevelProgressEntity entity, EntityGrapher<?> graph) {
    var attributs = graph.attributes();
    return SkillLevelProgress.toDomain(
        entity.getId(),
        attributs.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent(), graph.from("student"))
            : null,
        attributs.contains("skillLevel")
            ? SkillLevelMapper.INSTANCE.toDomain(entity.getSkillLevel(), graph.from("skillLevel"))
            : null,
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        attributs.contains("amses")
            ? entity.getAmses().stream()
                .map(e -> AMSMapper.INSTANCE.toDomain(e, graph.from("amses")))
                .toList()
            : List.of(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
