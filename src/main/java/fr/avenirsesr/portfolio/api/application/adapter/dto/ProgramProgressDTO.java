package fr.avenirsesr.portfolio.api.application.adapter.dto;

import java.util.List;
import java.util.UUID;

public record ProgramProgressDTO(UUID id, String name, List<SkillDTO> skills) {}
