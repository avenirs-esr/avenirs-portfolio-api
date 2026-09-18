package fr.avenirsesr.portfolio.student.program.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"associationId", "declaredProgram"})
public record DeclaredProgramAssociationDTO(
    UUID associationId, DeclaredProgramViewDTO declaredProgram) {}
