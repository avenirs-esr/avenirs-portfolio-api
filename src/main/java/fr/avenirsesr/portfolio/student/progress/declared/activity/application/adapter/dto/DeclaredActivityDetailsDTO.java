package fr.avenirsesr.portfolio.student.progress.declared.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.activity.application.adapter.dto.ActivityDTO;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.enums.EDeclaredActivityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(requiredProperties = {"id", "activity", "status", "createdAt", "updatedAt"})
public record DeclaredActivityDetailsDTO(
    UUID id,
    ActivityDTO activity,
    @Schema(ref = "#/components/schemas/EDeclaredActivityStatus") EDeclaredActivityStatus status,
    String reflection,
    LocalDate startDate,
    LocalDate endDate,
    Instant finishedAt,
    Instant createdAt,
    Instant updatedAt) {}
