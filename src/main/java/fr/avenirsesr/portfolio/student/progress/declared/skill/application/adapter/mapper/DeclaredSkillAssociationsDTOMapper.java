package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper.DeclaredActivityViewDTOMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillAssociationsData;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;

public interface DeclaredSkillAssociationsDTOMapper {
  static DeclaredSkillAssociationsDTO toDTO(DeclaredSkillAssociationsData associations) {
    return new DeclaredSkillAssociationsDTO(
        associations.traceAssociations().stream()
            .map(
                t ->
                    new TraceAssociationDTO(
                        t.associationId(), TraceOverviewMapper.toDTO(t.trace(), null)))
            .toList(),
        associations.declaredActivityAssociations().stream()
            .map(
                a ->
                    new DeclaredActivityAssociationDTO(
                        a.associationId(),
                        DeclaredActivityViewDTOMapper.toDTO(a.declaredActivity())))
            .toList());
  }
}
