package fr.avenirsesr.portfolio.student.progress.application.adapter.dto;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "pathSegments", "type", "level"})
public record AdditionalSkillProgressDTO(
    UUID id,
    String title,
    List<String> pathSegments,
    @Schema(ref = "#/components/schemas/EAdditionalSkillType") EAdditionalSkillType type,
    @Schema(ref = "#/components/schemas/EAdditionalSkillLevel") EAdditionalSkillLevel level,
    String description) {}
