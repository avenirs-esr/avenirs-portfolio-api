package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementDetailsDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SelfKnowledgeElementDetailsMapper {
  SelfKnowledgeElementDetailsDTO toDTO(SelfKnowledgeElement selfKnowledgeElement);
}
