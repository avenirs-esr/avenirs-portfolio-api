package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "status", "title", "organization"})
public record DeclaredProgramViewDTO(
    UUID id,
    @Schema(ref = "#/components/schemas/EProgramStatus") EProgramStatus status,
    String title,
    String organization,
    String result,
    boolean valorized) {}
