package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema()
public record DeclaredActivityAssessmentRequest(
    @Schema(nullable = true) String comment,
    @Schema(nullable = true) Integer grade,
    @Schema(nullable = true) Boolean validation) {}
