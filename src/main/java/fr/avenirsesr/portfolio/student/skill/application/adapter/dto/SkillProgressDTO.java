package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {"id", "skillId", "title", "pathSegments", "type", "level", "valorized"})
public record SkillProgressDTO(
    UUID id,
    UUID skillId,
    String title,
    List<String> pathSegments,
    @Schema(ref = "#/components/schemas/ESkillReferentialType") ESkillReferentialType type,
    @Schema(ref = "#/components/schemas/ESkillLevel") ESkillLevel level,
    String reflection,
    boolean valorized,
    SkillProgressAssociationCountDTO associationsCount) {}
