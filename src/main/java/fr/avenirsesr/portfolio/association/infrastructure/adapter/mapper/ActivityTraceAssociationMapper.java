package fr.avenirsesr.portfolio.association.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.association.infrastructure.adapter.model.ActivityTraceAssociationEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.mapper.DeclaredActivityMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;

public class ActivityTraceAssociationMapper
    implements Mapper<ActivityTraceAssociationEntity, ActivityTraceAssociation> {
  public static ActivityTraceAssociationMapper INSTANCE = new ActivityTraceAssociationMapper();

  @Override
  public ActivityTraceAssociationEntity fromDomain(ActivityTraceAssociation domain) {
    return ActivityTraceAssociationEntity.of(
        domain.getId(),
        DeclaredActivityMapper.INSTANCE.fromDomain(domain.getActivity()),
        TraceMapper.INSTANCE.fromDomain(domain.getTrace()),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public ActivityTraceAssociation toDomain(ActivityTraceAssociationEntity entity) {
    return ActivityTraceAssociation.toDomain(
        entity.getId(),
        DeclaredActivityMapper.INSTANCE.toDomain(entity.getActivity()),
        TraceMapper.INSTANCE.toDomain(entity.getTrace()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public ActivityTraceAssociation toDomain(
      ActivityTraceAssociationEntity entity, EntityGrapher<?> graph) {
    var attributs = graph.attributes();
    return ActivityTraceAssociation.toDomain(
        entity.getId(),
        attributs.contains("activity")
            ? DeclaredActivityMapper.INSTANCE.toDomain(entity.getActivity())
            : null,
        attributs.contains("trace") ? TraceMapper.INSTANCE.toDomain(entity.getTrace()) : null,
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
