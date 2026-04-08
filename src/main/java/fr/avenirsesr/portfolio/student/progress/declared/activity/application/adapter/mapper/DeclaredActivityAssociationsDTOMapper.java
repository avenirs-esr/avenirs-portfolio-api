package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociationsData;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;

public interface DeclaredActivityAssociationsDTOMapper {
  static DeclaredActivityAssociationsDTO toDTO(DeclaredActivityAssociationsData associations) {
    return new DeclaredActivityAssociationsDTO(
        associations.traceAssociations().stream()
            .map(
                t ->
                    new TraceAssociationDTO(
                        t.associationId(), TraceOverviewMapper.toDTO(t.trace(), null)))
            .toList(),
        associations.declaredSkillAssociations().stream()
            .map(
                s ->
                    new DeclaredSkillAssociationDTO(
                        s.associationId(),
                        DeclaredSkillProgressMapper.toDeclaredSkillProgressDTO(s.declaredSkill())))
            .toList());
  }
}
