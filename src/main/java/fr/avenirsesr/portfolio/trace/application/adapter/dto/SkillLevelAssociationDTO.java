package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "level", "status"})
public record SkillLevelAssociationDTO(
    UUID id,
    String title,
    String level,
    @Schema(ref = "#/components/schemas/ESkillLevelStatus") ESkillLevelStatus status,
    AmsAssociationDTO ams) {}
