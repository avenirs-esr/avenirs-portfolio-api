package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.AmsAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.SkillLevelAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationsDTO;
import fr.avenirsesr.portfolio.trace.domain.data.AmsAssociationData;
import fr.avenirsesr.portfolio.trace.domain.data.SkillLevelAssociationData;
import fr.avenirsesr.portfolio.trace.domain.data.TraceAssociationsData;

public interface TraceAssociationsMapper {
  static TraceAssociationsDTO toDTO(TraceAssociationsData traceAssociations) {
    return new TraceAssociationsDTO(
        traceAssociations.declaredActivityAssociations().stream()
            .map(
                association ->
                    new DeclaredActivityAssociationDTO(
                        association.associationId(),
                        DeclaredActivityViewDTOMapper.toDTO(association.declaredActivity())))
            .toList(),
        traceAssociations.declaredSkillAssociations().stream()
            .map(
                association ->
                    new DeclaredSkillAssociationDTO(
                        association.associationId(),
                        DeclaredSkillProgressMapper.toDeclaredSkillProgressDTO(
                            association.declaredSkill())))
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

  static AmsAssociationDTO toDTO(AmsAssociationData amsAssociation) {
    return amsAssociation != null
        ? new AmsAssociationDTO(
            amsAssociation.id(), amsAssociation.title(), amsAssociation.status())
        : null;
  }
}
