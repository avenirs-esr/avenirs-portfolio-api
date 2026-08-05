package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(
    requiredProperties = {
      "id",
      "title",
      "pathSegments",
      "type",
      "level",
      "valorized",
      "associationsCount"
    })
public record DeclaredSkillProgressDTO(
    UUID id,
    String title,
    List<String> pathSegments,
    @Schema(ref = "#/components/schemas/EExternalSkillType") EExternalSkillType type,
    @Schema(ref = "#/components/schemas/EDeclaredSkillLevel") EDeclaredSkillLevel level,
    String reflection,
    boolean valorized,
    DeclaredSkillAssociationCountDTO associationsCount) {}
