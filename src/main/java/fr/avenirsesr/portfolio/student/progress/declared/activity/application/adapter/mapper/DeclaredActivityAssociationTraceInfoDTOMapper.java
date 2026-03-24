package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationTraceInfoDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.TraceAssociationSearchInfoData;

public interface DeclaredActivityAssociationTraceInfoDTOMapper {
  static DeclaredActivityAssociationTraceInfoDTO toDTO(
      TraceAssociationSearchInfoData traceAssociationSearchInfoData) {
    return new DeclaredActivityAssociationTraceInfoDTO(
        traceAssociationSearchInfoData.id(),
        traceAssociationSearchInfoData.title(),
        traceAssociationSearchInfoData.disabled());
  }
}
