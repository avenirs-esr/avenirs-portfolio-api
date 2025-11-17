package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;

public interface SelfKnowledgeCategoryMapper {
  static SelfKnowledgeCategoryDTO toSelfKnowledgeCategoryDTO(
      SelfKnowledgeCategory selfKnowledgeCategory) {
    return new SelfKnowledgeCategoryDTO(
        selfKnowledgeCategory.getId(),
        selfKnowledgeCategory.getTitle(),
        selfKnowledgeCategory.getDescription(),
        selfKnowledgeCategory.getType());
  }
}
