package fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillEntity;

public class SkillMapper implements Mapper<SkillEntity, Skill> {
  public static final SkillMapper INSTANCE = new SkillMapper();

  @Override
  public SkillEntity fromDomain(Skill domain) {
    return SkillEntity.of(
        domain.getId(),
        SkillReferentialMapper.INSTANCE.fromDomain(domain.getReferential()),
        domain.getExternalCode(),
        domain.getTitle(),
        domain.getDescription().orElse(null),
        domain.getParent().map(this::fromDomain).orElse(null),
        domain.isAssessable(),
        domain.isArchived(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public Skill toDomain(SkillEntity entity) {
    return toDomainWithParent(entity, null);
  }

  public Skill toDomainWithParent(SkillEntity entity, Skill parent) {
    return Skill.toDomain(
        entity.getId(),
        SkillReferentialMapper.INSTANCE.toDomain(entity.getReferential()),
        entity.getExternalCode(),
        entity.getTitle(),
        entity.getDescription(),
        parent,
        entity.isAssessable(),
        entity.isArchived(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
