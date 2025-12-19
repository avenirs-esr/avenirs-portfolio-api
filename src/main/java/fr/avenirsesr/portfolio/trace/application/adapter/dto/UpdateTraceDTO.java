package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"title", "language", "isGroup"})
public record UpdateTraceDTO(
    @NotBlank @Size(max = TITLE_LENGTH) String title,
    @Schema(ref = "#/components/schemas/ELanguage") ELanguage language,
    boolean isGroup,
    String personalNote,
    String iaJustification) {}
