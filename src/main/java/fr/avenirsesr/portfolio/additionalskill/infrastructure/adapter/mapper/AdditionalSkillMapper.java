package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;

public interface AdditionalSkillMapper {

  static AdditionalSkill toDomain(AdditionalSkillEntity entity) {
    return AdditionalSkill.toDomain(
        entity.getId(),
        entity.getExternalSkillId(),
        entity.getLibelle(),
        entity.getType(),
        entity.getPathSegments(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static AdditionalSkillEntity fromDomain(AdditionalSkill domain) {
    return AdditionalSkillEntity.of(
        domain.getId(),
        domain.getExternalSkillId(),
        domain.getLibelle(),
        domain.getType(),
        domain.getPathSegments());
  }
}
