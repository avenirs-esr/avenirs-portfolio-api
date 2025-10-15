package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillCategoryEntity;

public interface AdditionalSkillCategoryMapper {
  static AdditionalSkillCategoryEntity fromDomain(AdditionalSkillCategory domain) {
    return AdditionalSkillCategoryEntity.of(
        domain.getId(),
        domain.getLibelle(),
        domain.getType(),
        domain.getParent().map(AdditionalSkillCategoryMapper::fromDomain).orElse(null));
  }

  static AdditionalSkillCategory toDomain(AdditionalSkillCategoryEntity entity) {
    return AdditionalSkillCategory.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getParent().map(AdditionalSkillCategoryMapper::toDomain).orElse(null),
        entity.getType(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
