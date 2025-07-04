package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.dto;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.ProgramEntity;
import java.util.UUID;

public record StudentTrainingPathSummaryDTO(UUID id, ProgramEntity program) {}
