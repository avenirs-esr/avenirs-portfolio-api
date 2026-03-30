package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "organization", "startDate", "createdAt", "updatedAt"})
public record DeclaredProgramDetailedDTO(
    UUID id,
    @Schema(ref = "#/components/schemas/EProgramStatus") EProgramStatus status,
    String title,
    String description,
    String organization,
    String result,
    String sourceOfInformation,
    LocalDate startDate,
    LocalDate endDate,
    Instant createdAt,
    Instant updatedAt) {}
