package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;

public class AdditionalSkillMapper implements Mapper<AdditionalSkillEntity, AdditionalSkill> {

  public static final AdditionalSkillMapper INSTANCE = new AdditionalSkillMapper();

  @Override
  public AdditionalSkill toDomain(AdditionalSkillEntity entity) {
    return AdditionalSkill.toDomain(
        entity.getId(),
        entity.getExternalSkillId(),
        entity.getLibelle(),
        entity.getType(),
        entity.getPathSegments(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public AdditionalSkillEntity fromDomain(AdditionalSkill domain) {
    return AdditionalSkillEntity.of(
        domain.getId(),
        domain.getExternalSkillId(),
        domain.getLibelle(),
        domain.getType(),
        domain.getPathSegments());
  }
}
