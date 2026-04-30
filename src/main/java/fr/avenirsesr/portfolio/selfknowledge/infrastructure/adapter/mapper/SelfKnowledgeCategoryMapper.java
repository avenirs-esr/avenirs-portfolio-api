package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;

public class SelfKnowledgeCategoryMapper
    implements Mapper<SelfKnowledgeCategoryEntity, SelfKnowledgeCategory> {
  public static SelfKnowledgeCategoryMapper INSTANCE = new SelfKnowledgeCategoryMapper();

  @Override
  public SelfKnowledgeCategoryEntity fromDomain(SelfKnowledgeCategory category) {
    var entity =
        SelfKnowledgeCategoryEntity.of(
            category.getId(),
            category.getType(),
            category.isMandatory(),
            category.getCreatedAt(),
            category.getUpdatedAt());

    TranslationUtil.addTranslation(
        category,
        entity,
        SelfKnowledgeCategoryTranslationEntity::create,
        SelfKnowledgeCategoryTranslationEntity::update);

    return entity;
  }

  @Override
  public SelfKnowledgeCategory toDomain(SelfKnowledgeCategoryEntity entity) {
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
}
