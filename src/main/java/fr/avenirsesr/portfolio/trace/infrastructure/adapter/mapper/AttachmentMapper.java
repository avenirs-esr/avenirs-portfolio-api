package fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;

public interface AttachmentMapper {
  static TraceAttachmentEntity fromDomain(TraceAttachment traceAttachment) {
    return TraceAttachmentEntity.of(
        traceAttachment.getId(),
        TraceMapper.fromDomain(traceAttachment.getTrace()),
        traceAttachment.getName(),
        traceAttachment.getAttachmentType(),
        traceAttachment.getSize(),
        traceAttachment.getVersion(),
        traceAttachment.isActiveVersion(),
        traceAttachment.getUploadedAt(),
        traceAttachment.getUri());
  }

  static TraceAttachment toDomain(TraceAttachmentEntity entity) {
    return TraceAttachment.toDomain(
        entity.getId(),
        TraceMapper.toDomain(entity.getTrace()),
        entity.getName(),
        entity.getAttachmentType(),
        entity.getSize(),
        entity.getVersion(),
        entity.isActiveVersion(),
        entity.getUploadedAt(),
        entity.getUri());
  }
}
