package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.AdditionalSkillAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.AmsAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.AssociationsTraceDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.SkillLevelAssociationDTO;
import fr.avenirsesr.portfolio.trace.domain.model.AdditionalSkillAssociation;
import fr.avenirsesr.portfolio.trace.domain.model.AmsAssociation;
import fr.avenirsesr.portfolio.trace.domain.model.AssociationsTrace;
import fr.avenirsesr.portfolio.trace.domain.model.SkillLevelAssociation;

public interface AssociationsTraceMapper {
  static AssociationsTraceDTO toDTO(AssociationsTrace associationsTrace) {
    return new AssociationsTraceDTO(
        associationsTrace.skillLevelAssociations().stream()
            .map(AssociationsTraceMapper::toDTO)
            .toList(),
        associationsTrace.additionalSkillAssociations().stream()
            .map(AssociationsTraceMapper::toDTO)
            .toList());
  }

  static SkillLevelAssociationDTO toDTO(SkillLevelAssociation skillLevelAssociation) {
    return skillLevelAssociation != null
        ? new SkillLevelAssociationDTO(
            skillLevelAssociation.id(),
            skillLevelAssociation.skillTitle(),
            skillLevelAssociation.level(),
            skillLevelAssociation.status(),
            toDTO(skillLevelAssociation.ams()))
        : null;
  }

  static AdditionalSkillAssociationDTO toDTO(
      AdditionalSkillAssociation additionalSkillAssociation) {
    return additionalSkillAssociation != null
        ? new AdditionalSkillAssociationDTO(
            additionalSkillAssociation.id(),
            additionalSkillAssociation.title(),
            additionalSkillAssociation.level(),
            additionalSkillAssociation.pathSegments(),
            additionalSkillAssociation.type())
        : null;
  }

  static AmsAssociationDTO toDTO(AmsAssociation amsAssociation) {
    return amsAssociation != null
        ? new AmsAssociationDTO(
            amsAssociation.id(), amsAssociation.title(), amsAssociation.status())
        : null;
  }
}
