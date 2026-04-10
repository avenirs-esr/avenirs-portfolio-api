package fr.avenirsesr.portfolio.association.application.adapter.mapper;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDTO;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredActivityDTO;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredSkillIDTO;
import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultTraceDTO;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;

public interface AssociationSearchResultDTOMapper {
  static AssociationSearchResultDTO toDTO(AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.category(),
        associationSearchResultData.disabled());
  }

  static AssociationSearchResultDeclaredActivityDTO toDeclaredActivityDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultDeclaredActivityDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.category() != null
            ? EActivityThematic.valueOf(associationSearchResultData.category())
            : null,
        associationSearchResultData.disabled());
  }

  static AssociationSearchResultDeclaredSkillIDTO toDeclaredSkillDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultDeclaredSkillIDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.category() != null
            ? EExternalSkillType.valueOf(associationSearchResultData.category())
            : null,
        associationSearchResultData.disabled());
  }

  static AssociationSearchResultTraceDTO toTraceDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultTraceDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.disabled());
  }
}
