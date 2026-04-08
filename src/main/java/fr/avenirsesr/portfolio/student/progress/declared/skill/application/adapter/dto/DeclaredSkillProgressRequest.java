package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.REFLECTION_LENGTH;

import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(requiredProperties = {"level", "reflection"})
public record DeclaredSkillProgressRequest(
    @Schema(ref = "#/components/schemas/EDeclaredSkillLevel") EDeclaredSkillLevel level,
    @Size(max = REFLECTION_LENGTH) String reflection) {}
