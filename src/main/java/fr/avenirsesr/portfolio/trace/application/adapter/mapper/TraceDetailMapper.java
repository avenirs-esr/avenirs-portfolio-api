package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.mapper.AttachmentUploadDTOMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.trace.domain.model.TraceDetail;

public interface TraceDetailMapper {
  static TraceDetailDTO toDTO(TraceDetail traceDetail) {
    return new TraceDetailDTO(
        traceDetail.id(),
        traceDetail.title(),
        traceDetail.status(),
        traceDetail.programName(),
        traceDetail.isGroup(),
        traceDetail.aiUseJustification(),
        traceDetail.personalNote(),
        AttachmentUploadDTOMapper.fromDomain(traceDetail.attachment()),
        AssociationsTraceMapper.toDTO(traceDetail.associationsTrace()),
        traceDetail.createdAt(),
        traceDetail.updatedAt());
  }
}
