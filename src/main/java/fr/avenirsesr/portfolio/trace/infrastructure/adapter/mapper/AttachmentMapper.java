package fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.trace.domain.model.Attachment;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.AttachmentEntity;

public interface AttachmentMapper {
  static AttachmentEntity fromDomain(Attachment attachment) {
    return AttachmentEntity.of(
        attachment.getId(),
        TraceMapper.fromDomain(attachment.getTrace()),
        attachment.getName(),
        attachment.getAttachmentType(),
        attachment.getSize(),
        attachment.getVersion(),
        attachment.isActiveVersion(),
        attachment.getUploadedAt(),
        attachment.getUri());
  }

  static Attachment toDomain(AttachmentEntity entity) {
    return Attachment.toDomain(
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
