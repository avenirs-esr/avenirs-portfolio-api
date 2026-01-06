package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.AmsAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.DeclaredSkillAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.SkillLevelAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationsDTO;
import fr.avenirsesr.portfolio.trace.domain.data.AmsAssociationData;
import fr.avenirsesr.portfolio.trace.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.trace.domain.data.SkillLevelAssociationData;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationsData;

public interface TraceAssociationsMapper {
  static TraceAssociationsDTO toDTO(TraceAssociationsData traceAssociations) {
    return new TraceAssociationsDTO(
        traceAssociations.skillLevelAssociations().stream()
            .map(TraceAssociationsMapper::toDTO)
            .toList(),
        traceAssociations.declaredSkillAssociations().stream()
            .map(TraceAssociationsMapper::toDTO)
            .toList());
  }

  static SkillLevelAssociationDTO toDTO(SkillLevelAssociationData skillLevelAssociation) {
    return skillLevelAssociation != null
        ? new SkillLevelAssociationDTO(
            skillLevelAssociation.id(),
            skillLevelAssociation.skillTitle(),
            skillLevelAssociation.level(),
            skillLevelAssociation.status(),
            toDTO(skillLevelAssociation.ams()))
        : null;
  }

  static DeclaredSkillAssociationDTO toDTO(DeclaredSkillAssociationData declaredSkillAssociation) {
    return declaredSkillAssociation != null
        ? new DeclaredSkillAssociationDTO(
            declaredSkillAssociation.id(),
            declaredSkillAssociation.title(),
            declaredSkillAssociation.level(),
            declaredSkillAssociation.pathSegments(),
            declaredSkillAssociation.type())
        : null;
  }

  static AmsAssociationDTO toDTO(AmsAssociationData amsAssociation) {
    return amsAssociation != null
        ? new AmsAssociationDTO(
            amsAssociation.id(), amsAssociation.title(), amsAssociation.status())
        : null;
  }
}
