package fr.avenirsesr.portfolio.student.progress.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "name", "levelCount", "currentSkillLevel", "isProgramFinished"})
public record SkillDTO(
    UUID id,
    String name,
    int levelCount,
    SkillLevelViewDTO currentSkillLevel,
    SkillLevelViewDTO achievedSkillLevels,
    boolean isProgramFinished) {}
