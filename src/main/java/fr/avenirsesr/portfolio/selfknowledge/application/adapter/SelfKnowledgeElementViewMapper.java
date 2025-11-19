package fr.avenirsesr.portfolio.selfknowledge.application.adapter;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementViewDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;

public interface SelfKnowledgeElementViewMapper {

  static SelfKnowledgeElementViewDTO toDTO(SelfKnowledgeElement selfKnowledgeElement) {
    return new SelfKnowledgeElementViewDTO(
        selfKnowledgeElement.getId(),
        selfKnowledgeElement.getTitle(),
        selfKnowledgeElement.getDescription(),
        selfKnowledgeElement.getRating());
  }
}
