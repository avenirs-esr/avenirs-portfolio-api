package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"traceAssociations"})
public record DeclaredActivityAssociationsDTO(
    List<DeclaredActivityTraceAssociationDTO> traceAssociations) {
  @Schema(requiredProperties = {"associationId, trace"})
  public record DeclaredActivityTraceAssociationDTO(UUID associationId, TraceOverviewDTO trace) {}
}
