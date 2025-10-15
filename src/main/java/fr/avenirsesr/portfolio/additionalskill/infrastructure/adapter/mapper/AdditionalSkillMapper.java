package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.*;

public interface AdditionalSkillMapper {

  static AdditionalSkill toDomain(AdditionalSkillEntity entity) {
    return AdditionalSkill.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getExternalId(),
        entity
            .getAdditionalSkillCategory()
            .map(AdditionalSkillCategoryMapper::toDomain)
            .orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static AdditionalSkillEntity fromDomain(AdditionalSkill domain) {
    return AdditionalSkillEntity.of(
        domain.getId(),
        domain.getExternalId(),
        domain.getLibelle(),
        domain.getType(),
        domain
            .getAdditionalSkillCategory()
            .map(AdditionalSkillCategoryMapper::fromDomain)
            .orElse(null));
  }
}
