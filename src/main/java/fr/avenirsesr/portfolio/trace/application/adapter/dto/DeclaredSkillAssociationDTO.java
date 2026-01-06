package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "level", "pathSegments", "type"})
public record DeclaredSkillAssociationDTO(
    UUID id,
    String title,
    @Schema(ref = "#/components/schemas/EDeclaredSkillLevel") EDeclaredSkillLevel level,
    List<String> pathSegments,
    @Schema(ref = "#/components/schemas/EExternalSkillType") EExternalSkillType type) {}
