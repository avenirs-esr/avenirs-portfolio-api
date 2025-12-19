package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "organization", "status"})
public record DeclaredProgramDTO(
    UUID id,
    String title,
    String organization,
    @Schema(ref = "#/components/schemas/EProgramStatus") EProgramStatus status) {}
