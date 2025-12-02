package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;

public interface SelfKnowledgeCategoryMapper {

  static SelfKnowledgeCategoryEntity fromDomain(SelfKnowledgeCategory category) {
    return SelfKnowledgeCategoryEntity.of(
        category.getId(), category.getType(), category.isMandatory());
  }

  static SelfKnowledgeCategory toDomain(SelfKnowledgeCategoryEntity entity) {
    SelfKnowledgeCategoryTranslationEntity translation =
        TranslationUtil.getTranslation(entity.getTranslations());

    return SelfKnowledgeCategory.toDomain(
        entity.getId(),
        translation.getTitle(),
        translation.getDescription(),
        entity.getType(),
        entity.isMandatory(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static SelfKnowledgeCategory toDomain(SelfKnowledgeCategoryTranslationEntity translation) {
    SelfKnowledgeCategoryEntity category = translation.getCategory();

    return SelfKnowledgeCategory.toDomain(
        category.getId(),
        translation.getTitle(),
        translation.getDescription(),
        category.getType(),
        category.isMandatory(),
        category.getCreatedAt(),
        category.getUpdatedAt());
  }
}
