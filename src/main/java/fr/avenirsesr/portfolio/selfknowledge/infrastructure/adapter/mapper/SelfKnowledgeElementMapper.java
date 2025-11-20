package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public interface SelfKnowledgeElementMapper {

  static SelfKnowledgeElementEntity fromDomain(SelfKnowledgeElement element) {
    return SelfKnowledgeElementEntity.of(
        element.getId(),
        StudentMapper.fromDomain(element.getStudent()),
        element.getTitle(),
        element.getDescription(),
        element.getRating(),
        SelfKnowledgeCategoryMapper.fromDomain(element.getSelfKnowledgeCategory()));
  }

  static SelfKnowledgeElement toDomain(SelfKnowledgeElementEntity entity) {
    return SelfKnowledgeElement.toDomain(
        entity.getId(),
        StudentMapper.toDomain(entity.getStudent()),
        entity.getTitle(),
        entity.getDescription(),
        entity.getRating(),
        SelfKnowledgeCategoryMapper.toDomain(entity.getSelfKnowledgeCategory()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
