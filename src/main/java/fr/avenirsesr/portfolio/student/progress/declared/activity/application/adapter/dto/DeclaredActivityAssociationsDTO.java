package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"traceAssociations", "declaredSkillAssociations"})
public record DeclaredActivityAssociationsDTO(
    List<DeclaredActivityTraceAssociationDTO> traceAssociations,
    List<DeclaredActivityDeclaredSkillAssociationDTO> declaredSkillAssociations) {
  @Schema(requiredProperties = {"associationId", "trace"})
  public record DeclaredActivityTraceAssociationDTO(UUID associationId, TraceOverviewDTO trace) {}

  @Schema(requiredProperties = {"associationId", "declaredSkill"})
  public record DeclaredActivityDeclaredSkillAssociationDTO(
      UUID associationId, DeclaredSkillProgressDTO declaredSkill) {}
}
