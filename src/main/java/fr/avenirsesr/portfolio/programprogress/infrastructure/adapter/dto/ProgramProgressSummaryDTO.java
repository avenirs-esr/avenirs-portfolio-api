package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.dto;

import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.ProgramEntity;
import java.util.UUID;

public record ProgramProgressSummaryDTO(UUID id, ProgramEntity program) {}
