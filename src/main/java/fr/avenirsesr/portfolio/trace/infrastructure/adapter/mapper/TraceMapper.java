package fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public class TraceMapper implements Mapper<TraceEntity, Trace> {
  public static final TraceMapper INSTANCE = new TraceMapper();

  @Override
  public TraceEntity fromDomain(Trace trace) {
    return TraceEntity.of(
        trace.getId(),
        UserMapper.INSTANCE.fromDomain(trace.getUser()),
        trace.getTitle(),
        trace.getLanguage(),
        trace.isGroup(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getLink().orElse(null),
        trace.getAttachment().map(FileMapper.INSTANCE::fromDomain).orElse(null),
        trace.getCreatedAt(),
        trace.getUpdatedAt(),
        trace.getDeletedAt().orElse(null));
  }

  @Override
  public Trace toDomain(TraceEntity traceEntity) {
    return Trace.toDomain(
        traceEntity.getId(),
        UserMapper.INSTANCE.toDomain(traceEntity.getUser()),
        traceEntity.getTitle(),
        traceEntity.isGroup(),
        traceEntity.getAiUseJustification(),
        traceEntity.getPersonalNote(),
        traceEntity.getLink(),
        traceEntity.getAttachment() == null
            ? null
            : FileMapper.INSTANCE.toDomain(traceEntity.getAttachment()),
        traceEntity.getCreatedAt(),
        traceEntity.getUpdatedAt(),
        traceEntity.getDeletedAt(),
        traceEntity.getLanguage());
  }

  @Override
  public Trace toDomain(TraceEntity traceEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return Trace.toDomain(
        traceEntity.getId(),
        attributes.contains("user") ? UserMapper.INSTANCE.toDomain(traceEntity.getUser()) : null,
        traceEntity.getTitle(),
        traceEntity.isGroup(),
        traceEntity.getAiUseJustification(),
        traceEntity.getPersonalNote(),
        traceEntity.getLink(),
        attributes.contains("attachment")
            ? FileMapper.INSTANCE.toDomain(traceEntity.getAttachment())
            : null,
        traceEntity.getCreatedAt(),
        traceEntity.getUpdatedAt(),
        traceEntity.getDeletedAt(),
        traceEntity.getLanguage());
  }
}
