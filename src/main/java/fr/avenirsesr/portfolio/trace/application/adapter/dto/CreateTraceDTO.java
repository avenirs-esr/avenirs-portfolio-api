package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"title", "language", "isGroup"})
public record CreateTraceDTO(
    @NotBlank @Size(max = 70) String title,
    @Schema(implementation = ELanguage.class) ELanguage language,
    boolean isGroup,
    String personalNote,
    String iaJustification) {}
