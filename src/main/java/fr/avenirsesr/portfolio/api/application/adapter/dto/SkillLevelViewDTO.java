package fr.avenirsesr.portfolio.api.application.adapter.dto;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "name", "shortDescription", "status"})
public record SkillLevelViewDTO(
    UUID id,
    String name,
    String shortDescription,
    @Schema(ref = "#/components/schemas/SkillLevelStatus") ESkillLevelStatus status) {}
