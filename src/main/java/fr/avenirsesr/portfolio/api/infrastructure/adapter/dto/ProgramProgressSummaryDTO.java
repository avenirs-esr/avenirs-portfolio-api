package fr.avenirsesr.portfolio.api.infrastructure.adapter.dto;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramEntity;
import java.util.UUID;

public record ProgramProgressSummaryDTO(UUID id, ProgramEntity program) {}
