package fr.avenirsesr.portfolio.additionalskill.application.adapter.dto;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"libelle", "type"})
public record AdditionalSkillCategoryDTO(
    String libelle,
    @Schema(ref = "#/components/schemas/EAdditionalSkillCategoryType")
        EAdditionalSkillCategoryType type) {}
