package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "thematic", "summary", "isNew"})
public record ActivityOverviewDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EActivityThematic") EActivityThematic thematic,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus") EDeclaredActivityStatus status,
    String summary,
    String executionPeriodInfoSummary,
    boolean isNew) {}
