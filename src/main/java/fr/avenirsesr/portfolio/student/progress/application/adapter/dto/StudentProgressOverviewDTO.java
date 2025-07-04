package fr.avenirsesr.portfolio.student.progress.application.adapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(requiredProperties = {"id", "name", "skills"})
public record StudentProgressOverviewDTO(UUID id, String name, List<SkillOverviewDTO> skills) {}
