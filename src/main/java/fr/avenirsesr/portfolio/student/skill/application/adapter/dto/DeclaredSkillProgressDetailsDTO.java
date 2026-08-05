package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceOverviewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "pathSegments",
      "type",
      "level",
      "traceAssociations",
      "createdAt",
      "updatedAt",
      "valorized"
    })
public record DeclaredSkillProgressDetailsDTO(
    UUID id,
    String title,
    List<DeclaredSkillCategoryDTO> pathSegments,
    String reflection,
    @Schema(ref = "#/components/schemas/EExternalSkillType") EExternalSkillType type,
    @Schema(ref = "#/components/schemas/EDeclaredSkillLevel") EDeclaredSkillLevel level,
    List<TraceOverviewDTO> traceAssociations,
    Instant createdAt,
    Instant updatedAt,
    boolean valorized) {}
