package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDeclaredSkillInfoDTO;
import fr.avenirsesr.portfolio.trace.domain.data.DeclaredSkillAssociationSearchInfoData;

public interface TraceAssociationDeclaredSkillInfoDTOMapper {
  static TraceAssociationDeclaredSkillInfoDTO toDTO(
      DeclaredSkillAssociationSearchInfoData declaredSkillAssociationSearchInfoData) {
    return new TraceAssociationDeclaredSkillInfoDTO(
        declaredSkillAssociationSearchInfoData.id(),
        declaredSkillAssociationSearchInfoData.title(),
        declaredSkillAssociationSearchInfoData.type(),
        declaredSkillAssociationSearchInfoData.disabled());
  }
}
