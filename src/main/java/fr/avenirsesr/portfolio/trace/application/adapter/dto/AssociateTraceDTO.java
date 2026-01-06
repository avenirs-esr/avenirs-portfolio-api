package fr.avenirsesr.portfolio.trace.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"amsIds", "skillIds", "declaredSkillProgressIds"})
public record AssociateTraceDTO(
    List<UUID> amsIds, List<UUID> skillLevelIds, List<UUID> declaredSkillProgressIds) {}
