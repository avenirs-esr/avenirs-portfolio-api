package fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementViewDTO;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {SelfKnowledgeCategoryMapper.class})
public interface SelfKnowledgeElementViewMapper {
  @Mapping(source = "selfKnowledgeCategory", target = "category")
  SelfKnowledgeElementViewDTO toDTO(SelfKnowledgeElement selfKnowledgeElement);
}
