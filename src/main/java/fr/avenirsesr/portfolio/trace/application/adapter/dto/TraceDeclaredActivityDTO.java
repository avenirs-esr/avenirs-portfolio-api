package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record TraceDeclaredActivityDTO(
    UUID activityId,
    String activityTitle,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus")
        EDeclaredActivityStatus activityStatus) {}
