package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public interface TraceAttachmentMapper {
  static TraceAttachmentEntity fromDomain(TraceAttachment traceAttachment) {
    return TraceAttachmentEntity.of(
        traceAttachment.getId(),
        TraceMapper.fromDomain(traceAttachment.getTrace()),
        traceAttachment.getName(),
        traceAttachment.getFileType(),
        traceAttachment.getSize(),
        traceAttachment.getVersion(),
        traceAttachment.isActiveVersion(),
        traceAttachment.getUri(),
        UserMapper.fromDomain(traceAttachment.getUploadedBy()),
        traceAttachment.getUploadedAt());
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
        entity.getUri(),
        UserMapper.toDomain(entity.getUploadedBy()),
        entity.getUploadedAt());
  }
}
