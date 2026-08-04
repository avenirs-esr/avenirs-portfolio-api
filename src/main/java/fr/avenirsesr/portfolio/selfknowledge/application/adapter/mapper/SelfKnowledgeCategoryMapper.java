package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SelfKnowledgeCategoryMapper {
  default SelfKnowledgeCategoryDTO toSelfKnowledgeCategoryDTO(ESelfKnowledgeCategory category) {
    return new SelfKnowledgeCategoryDTO(category, category.isMandatory());
  }
}
