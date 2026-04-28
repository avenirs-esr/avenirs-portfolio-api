package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SelfKnowledgeCategoryMapper {
  SelfKnowledgeCategoryDTO toSelfKnowledgeCategoryDTO(SelfKnowledgeCategory selfKnowledgeCategory);
}
