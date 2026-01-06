package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"level", "description"})
public record DeclaredSkillProgressRequest(
    @Schema(ref = "#/components/schemas/EDeclaredSkillLevel") EDeclaredSkillLevel level,
    @Size(max = 400) String description) {}
