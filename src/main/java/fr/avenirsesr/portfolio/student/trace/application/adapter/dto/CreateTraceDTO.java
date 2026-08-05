package fr.avenirsesr.portfolio.student.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"title", "language", "authorType"})
public record CreateTraceDTO(
    @NotBlank @Size(max = 70) String title,
    @Schema(ref = "#/components/schemas/ELanguage") ELanguage language,
    @Schema(ref = "#/components/schemas/ETraceAuthorType") ETraceAuthorType authorType,
    String personalNote,
    String iaJustification,
    String link) {}
