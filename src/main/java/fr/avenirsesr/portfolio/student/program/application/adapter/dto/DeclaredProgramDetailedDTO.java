package fr.avenirsesr.portfolio.student.program.application.adapter.dto;

import fr.avenirsesr.portfolio.student.program.domain.model.enums.EProgramStatus;
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
    boolean valorized,
    Instant createdAt,
    Instant updatedAt) {}
