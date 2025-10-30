package fr.avenirsesr.portfolio.student.progress.application.adapter.dto;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"level", "description"})
public record AdditionalSkillProgressRequest(
    @Schema(ref = "#/components/schemas/EAdditionalSkillLevel") EAdditionalSkillLevel level,
    @Size(max = 400) String description) {}
