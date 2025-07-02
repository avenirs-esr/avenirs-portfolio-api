package fr.avenirsesr.portfolio.programprogress.application.adapter.dto;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EDurationUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(requiredProperties = {"id", "name", "durationUnit", "durationCount"})
public record ProgramProgressDTO(
    UUID id, String name, EDurationUnit durationUnit, Integer durationCount) {}
