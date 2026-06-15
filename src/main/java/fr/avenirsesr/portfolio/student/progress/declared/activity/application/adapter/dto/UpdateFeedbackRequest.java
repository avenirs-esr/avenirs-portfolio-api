package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"feedback"})
public record UpdateFeedbackRequest(
    @Size(max = RICH_TEXT_LENGTH, message = "Feedback can not exceed {max} characters")
        String feedback) {}
