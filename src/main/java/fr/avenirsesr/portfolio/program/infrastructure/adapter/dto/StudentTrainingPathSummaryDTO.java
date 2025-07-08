package fr.avenirsesr.portfolio.program.infrastructure.adapter.dto;

import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.ProgramEntity;
import java.util.UUID;

public record StudentTrainingPathSummaryDTO(UUID id, ProgramEntity program) {}
