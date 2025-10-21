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
        AttachmentUploadDTOMapper.fromDomain(traceDetail.attachment()),
        TraceAssociationsMapper.toDTO(traceDetail.traceAssociations()),
        traceDetail.createdAt(),
        traceDetail.updatedAt());
  }
}
