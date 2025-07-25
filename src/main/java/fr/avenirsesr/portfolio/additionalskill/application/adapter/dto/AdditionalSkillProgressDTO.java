package fr.avenirsesr.portfolio.additionalskill.application.adapter.dto;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"id", "title", "pathSegments", "type", "level"})
public record AdditionalSkillProgressDTO(
    UUID id, String title, List<String> pathSegments, String type, EAdditionalSkillLevel level) {}
