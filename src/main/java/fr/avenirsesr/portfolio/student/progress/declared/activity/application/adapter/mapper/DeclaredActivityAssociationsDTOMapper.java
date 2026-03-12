package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.mapper;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.data.DeclaredActivityAssociations;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;

public interface DeclaredActivityAssociationsDTOMapper {
  static DeclaredActivityAssociationsDTO toDTO(DeclaredActivityAssociations associations) {
    return new DeclaredActivityAssociationsDTO(
        associations.traceAssociations().stream()
            .map(
                t ->
                    new DeclaredActivityAssociationsDTO.DeclaredActivityTraceAssociationDTO(
                        t.getId(), TraceOverviewMapper.toDTO(t.getTrace(), null)))
            .toList());
  }
}
