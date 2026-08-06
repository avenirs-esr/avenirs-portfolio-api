package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class TraceMapper implements Mapper<TraceEntity, Trace> {
  public static final TraceMapper INSTANCE = new TraceMapper();

  @Override
  public TraceEntity fromDomain(Trace trace) {
    return TraceEntity.of(
        trace.getId(),
        StudentMapper.INSTANCE.fromDomain(trace.getStudent()),
        trace.getTitle(),
        trace.getLanguage(),
        trace.getAuthorType(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getLink().orElse(null),
        trace.getAttachment().map(FileMapper.INSTANCE::fromDomain).orElse(null),
        trace.isValorized(),
        trace.getCreatedAt(),
        trace.getUpdatedAt());
  }

  @Override
  public Trace toDomain(TraceEntity traceEntity) {
    return Trace.toDomain(
        traceEntity.getId(),
        StudentMapper.INSTANCE.toDomain(traceEntity.getStudent()),
        traceEntity.getTitle(),
        traceEntity.getAuthorType(),
        traceEntity.getAiUseJustification(),
        traceEntity.getPersonalNote(),
        traceEntity.getLink(),
        traceEntity.getAttachment() == null
            ? null
            : FileMapper.INSTANCE.toDomain(traceEntity.getAttachment()),
        traceEntity.getCreatedAt(),
        traceEntity.getUpdatedAt(),
        traceEntity.getLanguage(),
        traceEntity.isValorized());
  }

  @Override
  public Trace toDomain(TraceEntity traceEntity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return Trace.toDomain(
        traceEntity.getId(),
        attributes.contains("student")
            ? StudentMapper.INSTANCE.toDomain(traceEntity.getStudent())
            : null,
        traceEntity.getTitle(),
        traceEntity.getAuthorType(),
        traceEntity.getAiUseJustification(),
        traceEntity.getPersonalNote(),
        traceEntity.getLink(),
        attributes.contains("attachment")
            ? FileMapper.INSTANCE.toDomain(traceEntity.getAttachment())
            : null,
        traceEntity.getCreatedAt(),
        traceEntity.getUpdatedAt(),
        traceEntity.getLanguage(),
        traceEntity.isValorized());
  }
}
