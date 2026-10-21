package fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillReferentialEntity;

public class SkillReferentialMapper implements Mapper<SkillReferentialEntity, SkillReferential> {
  public static final SkillReferentialMapper INSTANCE = new SkillReferentialMapper();

  @Override
  public SkillReferentialEntity fromDomain(SkillReferential domain) {
    return SkillReferentialEntity.of(
        domain.getId(),
        domain.getType(),
        domain.getExternalId(),
        domain.getTitle(),
        domain.getInstitutionId().orElse(null),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public SkillReferential toDomain(SkillReferentialEntity entity) {
    return SkillReferential.toDomain(
        entity.getId(),
        entity.getType(),
        entity.getExternalId(),
        entity.getTitle(),
        entity.getInstitutionId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
