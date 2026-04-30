package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeElementEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class SelfKnowledgeElementMapper
    implements Mapper<SelfKnowledgeElementEntity, SelfKnowledgeElement> {
  public static final SelfKnowledgeElementMapper INSTANCE = new SelfKnowledgeElementMapper();

  @Override
  public SelfKnowledgeElementEntity fromDomain(SelfKnowledgeElement element) {
    return SelfKnowledgeElementEntity.of(
        element.getId(),
        StudentMapper.INSTANCE.fromDomain(element.getStudent()),
        element.getTitle(),
        element.getDescription(),
        element.getRating(),
        SelfKnowledgeCategoryMapper.INSTANCE.fromDomain(element.getSelfKnowledgeCategory()),
        element.getCreatedAt(),
        element.getUpdatedAt());
  }

  @Override
  public SelfKnowledgeElement toDomain(SelfKnowledgeElementEntity entity) {
    return SelfKnowledgeElement.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        entity.getTitle(),
        entity.getDescription(),
        entity.getRating(),
        SelfKnowledgeCategoryMapper.INSTANCE.toDomain(entity.getSelfKnowledgeCategory()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
