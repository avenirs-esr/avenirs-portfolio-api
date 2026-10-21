package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.DeclaredSkillProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class DeclaredSkillProgressMapper
    implements Mapper<DeclaredSkillProgressEntity, DeclaredSkillProgress> {
  public static final DeclaredSkillProgressMapper INSTANCE = new DeclaredSkillProgressMapper();

  @Override
  public DeclaredSkillProgressEntity fromDomain(DeclaredSkillProgress declaredSkillProgress) {
    return DeclaredSkillProgressEntity.of(
        declaredSkillProgress.getId(),
        StudentMapper.INSTANCE.fromDomain(declaredSkillProgress.getStudent()),
        SkillMapper.INSTANCE.fromDomain(declaredSkillProgress.getSkill()),
        declaredSkillProgress.getLevel(),
        declaredSkillProgress.getReflection(),
        declaredSkillProgress.isValorized(),
        declaredSkillProgress.getCreatedAt(),
        declaredSkillProgress.getUpdatedAt());
  }

  @Override
  public DeclaredSkillProgress toDomain(DeclaredSkillProgressEntity entity) {
    return DeclaredSkillProgress.toDomain(
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
  public DeclaredSkillProgress toDomain(
      DeclaredSkillProgressEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return DeclaredSkillProgress.toDomain(
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
