package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"id"})
public record UpdateReflectionRequest(
    @Size(max = RICH_TEXT_LENGTH, message = "Reflection can not exceed {max} characters")
        String reflection) {}
