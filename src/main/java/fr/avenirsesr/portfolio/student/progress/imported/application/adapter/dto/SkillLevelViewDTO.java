package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "name",
      "shortDescription",
      "traceCount",
      "activityCount",
      "status"
    })
public record SkillLevelViewDTO(
    UUID id,
    String name,
    String shortDescription,
    int traceCount,
    int activityCount,
    @Schema(ref = "#/components/schemas/ESkillLevelStatus") ESkillLevelStatus status) {}
