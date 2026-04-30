package fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.TraceAttachmentEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;

public class TraceAttachmentMapper implements Mapper<TraceAttachmentEntity, TraceAttachment> {
  public static final TraceAttachmentMapper INSTANCE = new TraceAttachmentMapper();

  @Override
  public TraceAttachmentEntity fromDomain(TraceAttachment traceAttachment) {
    return TraceAttachmentEntity.of(
        traceAttachment.getId(),
        TraceMapper.INSTANCE.fromDomain(traceAttachment.getTrace()),
        traceAttachment.getName(),
        traceAttachment.getFileType(),
        traceAttachment.getSize(),
        traceAttachment.getVersion(),
        traceAttachment.isActiveVersion(),
        traceAttachment.getUri(),
        UserMapper.INSTANCE.fromDomain(traceAttachment.getUploadedBy()),
        traceAttachment.getUploadedAt(),
        traceAttachment.getCreatedAt(),
        traceAttachment.getUpdatedAt());
  }

  @Override
  public TraceAttachment toDomain(TraceAttachmentEntity entity) {
    return TraceAttachment.toDomain(
        entity.getId(),
        TraceMapper.INSTANCE.toDomain(entity.getTrace()),
        entity.getName(),
        entity.getFileType(),
        entity.getSize(),
        entity.getVersion(),
        entity.isActiveVersion(),
        entity.getUri(),
        UserMapper.INSTANCE.toDomain(entity.getUploadedBy()),
        entity.getUploadedAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
