package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"declaredActivityAssociations"})
public record TraceAssociationsDTO(
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations) {
  @Schema(requiredProperties = {"associationId, declaredActivity"})
  public record DeclaredActivityAssociationDTO(
      UUID associationId, DeclaredActivityViewDTO declaredActivity) {}
}
