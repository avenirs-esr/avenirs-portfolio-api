package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDeclaredActivityInfoDTO;
import fr.avenirsesr.portfolio.trace.domain.data.DeclaredActivityAssociationSearchInfoData;

public interface TraceAssociationDeclaredActivityInfoDTOMapper {
  static TraceAssociationDeclaredActivityInfoDTO toDTO(
      DeclaredActivityAssociationSearchInfoData declaredActivityAssociationSearchInfoData) {
    return new TraceAssociationDeclaredActivityInfoDTO(
        declaredActivityAssociationSearchInfoData.id(),
        declaredActivityAssociationSearchInfoData.title(),
        declaredActivityAssociationSearchInfoData.thematic(),
        declaredActivityAssociationSearchInfoData.disabled());
  }
}
