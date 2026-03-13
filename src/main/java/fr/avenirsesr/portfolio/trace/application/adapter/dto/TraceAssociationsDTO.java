package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"declaredActivityAssociations"})
public record TraceAssociationsDTO(
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations,
    List<DeclaredSkillAssociationDTO> declaredSkillAssociations) {

  @Schema(requiredProperties = {"associationId, declaredActivity"})
  public record DeclaredActivityAssociationDTO(
      UUID associationId, DeclaredActivityViewDTO declaredActivity) {}

  @Schema(requiredProperties = {"associationId, declaredSkill"})
  public record DeclaredSkillAssociationDTO(
      UUID associationId, DeclaredSkillProgressDTO declaredSkill) {}
}
