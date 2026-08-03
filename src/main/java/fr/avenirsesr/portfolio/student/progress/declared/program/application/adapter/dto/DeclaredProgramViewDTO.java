package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(requiredProperties = {"id", "status", "title", "organization", "valorized"})
public record DeclaredProgramViewDTO(
    UUID id,
    @Schema(ref = "#/components/schemas/EProgramStatus") EProgramStatus status,
    String title,
    String description,
    String organization,
    String result,
    LocalDate startDate,
    LocalDate endDate,
    boolean valorized) {}
