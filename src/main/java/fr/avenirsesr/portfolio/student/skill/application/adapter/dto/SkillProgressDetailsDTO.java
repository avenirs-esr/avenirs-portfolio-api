package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.TraceOverviewDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "skillId",
      "title",
      "pathSegments",
      "type",
      "level",
      "traceAssociations",
      "createdAt",
      "updatedAt",
      "valorized"
    })
public record SkillProgressDetailsDTO(
    UUID id,
    UUID skillId,
    String title,
    List<SkillPathSegmentDTO> pathSegments,
    String description,
    String reflection,
    @Schema(ref = "#/components/schemas/ESkillReferentialType") ESkillReferentialType type,
    @Schema(ref = "#/components/schemas/ESkillLevel") ESkillLevel level,
    List<TraceOverviewDTO> traceAssociations,
    Instant createdAt,
    Instant updatedAt,
    boolean valorized) {}
