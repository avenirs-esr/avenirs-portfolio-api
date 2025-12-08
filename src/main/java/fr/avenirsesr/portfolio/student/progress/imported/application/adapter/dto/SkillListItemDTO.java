package fr.avenirsesr.portfolio.student.progress.imported.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"skillId", "title"})
public record SkillListItemDTO(UUID skillId, String title) {}
