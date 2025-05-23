package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.List;
import java.util.UUID;

public record SkillDTO(
    UUID id, String name, int trackCount, int activityCount, List<SkillLevelDTO> levels) {}
