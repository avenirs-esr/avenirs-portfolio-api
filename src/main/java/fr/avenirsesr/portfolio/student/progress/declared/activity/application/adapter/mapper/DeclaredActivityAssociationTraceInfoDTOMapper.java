package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationTraceInfoDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.TraceInfoData;

public interface DeclaredActivityAssociationTraceInfoDTOMapper {
  static DeclaredActivityAssociationTraceInfoDTO toDTO(TraceInfoData traceInfoData) {
    return new DeclaredActivityAssociationTraceInfoDTO(
        traceInfoData.id(), traceInfoData.title(), traceInfoData.disabled());
  }
}
