package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.SkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class SkillProgressMapper implements Mapper<SkillProgressEntity, SkillProgress> {
  public static final SkillProgressMapper INSTANCE = new SkillProgressMapper();

  @Override
  public SkillProgressEntity fromDomain(SkillProgress skillProgress) {
    return SkillProgressEntity.of(
        skillProgress.getId(),
        StudentMapper.INSTANCE.fromDomain(skillProgress.getStudent()),
        SkillMapper.INSTANCE.fromDomain(skillProgress.getSkill()),
        skillProgress.getLevel(),
        skillProgress.getReflection(),
        skillProgress.isValorized(),
        skillProgress.getCreatedAt(),
        skillProgress.getUpdatedAt());
  }

  @Override
  public SkillProgress toDomain(SkillProgressEntity entity) {
    return SkillProgress.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        SkillMapper.INSTANCE.toDomain(entity.getSkill()),
        entity.getLevel(),
        entity.getReflection(),
        entity.isValorized(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public SkillProgress toDomain(SkillProgressEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return SkillProgress.toDomain(
        entity.getId(),
        attributes.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent())
            : null,
        attributes.contains("skill") ? SkillMapper.INSTANCE.toDomain(entity.getSkill()) : null,
        entity.getLevel(),
        entity.getReflection(),
        entity.isValorized(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
