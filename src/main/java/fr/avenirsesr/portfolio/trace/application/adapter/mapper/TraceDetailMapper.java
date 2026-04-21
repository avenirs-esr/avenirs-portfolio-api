package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.file.application.adapter.mapper.AttachmentUploadDTOMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceDetailDTO;
import fr.avenirsesr.portfolio.trace.domain.data.TraceDetailData;

public interface TraceDetailMapper {
  static TraceDetailDTO toDTO(TraceDetailData traceDetail) {
    return new TraceDetailDTO(
        traceDetail.id(),
        traceDetail.title(),
        traceDetail.isAssociated(),
        traceDetail.programName(),
        traceDetail.isGroup(),
        traceDetail.aiUseJustification(),
        traceDetail.personalNote(),
        traceDetail.link().orElse(null),
        traceDetail.attachment().map(AttachmentUploadDTOMapper::fromDomain).orElse(null),
        traceDetail.createdAt(),
        traceDetail.updatedAt());
  }
}
