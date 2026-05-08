package fr.avenirsesr.portfolio.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema()
public record ActivityDraftUpdateRequest(
    String title,
    EActivityThematic thematic,
    String summary,
    String description,
    String executionPeriodInfo,
    String executionPeriodInfoSummary,
    Integer traceAllowedAssociations,
    Integer feedbackAllowedIterations,
    Boolean enableReflection) {}
