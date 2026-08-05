package fr.avenirsesr.portfolio.student.association.application.adapter.mapper;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredActivityDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredExperienceDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDeclaredSkillIDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultTraceDTO;
import fr.avenirsesr.portfolio.student.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssociationSearchResultDTOMapper {

  AssociationSearchResultDTO toDTO(AssociationSearchResultData associationSearchResultData);

  default AssociationSearchResultDeclaredActivityDTO toDeclaredActivityDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultDeclaredActivityDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.category() != null
            ? EActivityThematic.valueOf(associationSearchResultData.category())
            : null,
        associationSearchResultData.disabled());
  }

  default AssociationSearchResultDeclaredSkillIDTO toDeclaredSkillDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultDeclaredSkillIDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.category() != null
            ? EExternalSkillType.valueOf(associationSearchResultData.category())
            : null,
        associationSearchResultData.disabled());
  }

  default AssociationSearchResultDeclaredExperienceDTO toDeclaredExperienceDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultDeclaredExperienceDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.category() != null
            ? EExperienceType.valueOf(associationSearchResultData.category())
            : null,
        associationSearchResultData.disabled());
  }

  default AssociationSearchResultTraceDTO toTraceDTO(
      AssociationSearchResultData associationSearchResultData) {
    return new AssociationSearchResultTraceDTO(
        associationSearchResultData.id(),
        associationSearchResultData.title(),
        associationSearchResultData.disabled());
  }
}
