package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.List;
import java.util.UUID;

public record ProgramProgressOverviewDTO(UUID id, String name, List<SkillOverviewDTO> skills) {}
