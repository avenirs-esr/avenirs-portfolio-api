package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto;

import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceAssociationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(requiredProperties = {"traceAssociations"})
public record DeclaredExperienceAssociationsDTO(List<TraceAssociationDTO> traceAssociations) {}
