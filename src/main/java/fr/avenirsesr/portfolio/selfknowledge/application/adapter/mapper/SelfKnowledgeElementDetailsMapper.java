package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementDetailsDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;

public interface SelfKnowledgeElementDetailsMapper {

  static SelfKnowledgeElementDetailsDTO toDTO(SelfKnowledgeElement selfKnowledgeElement) {
    return new SelfKnowledgeElementDetailsDTO(
        selfKnowledgeElement.getId(),
        selfKnowledgeElement.getTitle(),
        selfKnowledgeElement.getDescription(),
        selfKnowledgeElement.getRating(),
        selfKnowledgeElement.getCreatedAt(),
        selfKnowledgeElement.getUpdatedAt());
  }
}
