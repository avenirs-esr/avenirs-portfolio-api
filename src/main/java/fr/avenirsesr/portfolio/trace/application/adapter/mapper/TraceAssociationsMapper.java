package fr.avenirsesr.portfolio.trace.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationsDTO;
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
            .toList(),
        traceAssociations.declaredExperienceAssociations().stream()
            .map(
                association ->
                    new DeclaredExperienceAssociationDTO(
                        association.associationId(),
                        DeclaredExperienceMapper.toDTO(association.declaredExperience())))
            .toList());
  }
}
