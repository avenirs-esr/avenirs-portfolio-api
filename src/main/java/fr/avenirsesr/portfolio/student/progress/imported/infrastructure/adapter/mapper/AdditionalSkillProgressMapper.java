package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class AdditionalSkillProgressMapper
    implements Mapper<AdditionalSkillProgressEntity, AdditionalSkillProgress> {
  public static final AdditionalSkillProgressMapper INSTANCE = new AdditionalSkillProgressMapper();

  @Override
  public AdditionalSkillProgressEntity fromDomain(AdditionalSkillProgress additionalSkillProgress) {
    return AdditionalSkillProgressEntity.create(
        additionalSkillProgress.getId(),
        StudentMapper.INSTANCE.fromDomain(additionalSkillProgress.getStudent()),
        AdditionalSkillMapper.INSTANCE.fromDomain(additionalSkillProgress.getSkill()),
        additionalSkillProgress.getLevel(),
        additionalSkillProgress.getDescription());
  }

  @Override
  public AdditionalSkillProgress toDomain(AdditionalSkillProgressEntity entity) {
    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        AdditionalSkillMapper.INSTANCE.toDomain(entity.getAdditionalSkill()),
        entity.getLevel(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public AdditionalSkillProgress toDomain(
      AdditionalSkillProgressEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return AdditionalSkillProgress.toDomain(
        entity.getId(),
        attributes.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent())
            : null,
        attributes.contains("additionalSkill")
            ? AdditionalSkillMapper.INSTANCE.toDomain(entity.getAdditionalSkill())
            : null,
        entity.getLevel(),
        entity.getDescription(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
