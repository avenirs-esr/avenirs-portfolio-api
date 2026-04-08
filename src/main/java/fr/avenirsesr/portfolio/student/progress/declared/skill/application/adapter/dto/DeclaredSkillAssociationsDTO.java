package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto.DeclaredActivityAssociationDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"traceAssociations", "declaredActivityAssociations"})
public record DeclaredSkillAssociationsDTO(
    List<TraceAssociationDTO> traceAssociations,
    List<DeclaredActivityAssociationDTO> declaredActivityAssociations) {}
