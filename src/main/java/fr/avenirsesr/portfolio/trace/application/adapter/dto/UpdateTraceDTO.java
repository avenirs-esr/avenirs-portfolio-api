package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.TITLE_LENGTH;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"title", "language", "authorType", "valorized"})
public record UpdateTraceDTO(
    @NotBlank @Size(max = TITLE_LENGTH) String title,
    @Schema(ref = "#/components/schemas/ELanguage") ELanguage language,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    String personalNote,
    String iaJustification,
    String link,
    boolean valorized) {}
