package fr.avenirsesr.portfolio.student.skill.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"associationId", "skillProgress"})
public record SkillProgressAssociationDTO(UUID associationId, SkillProgressDTO skillProgress) {}
