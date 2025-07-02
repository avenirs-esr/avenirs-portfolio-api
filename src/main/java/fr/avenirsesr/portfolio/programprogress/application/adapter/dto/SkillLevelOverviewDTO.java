package fr.avenirsesr.portfolio.programprogress.application.adapter.dto;

import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "name", "status"})
public record SkillLevelOverviewDTO(
    UUID id,
    String name,
    @Schema(ref = "#/components/schemas/SkillLevelStatus") ESkillLevelStatus status) {}
