package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.temporal.domain.model.enums.EDurationUnit;
import java.util.List;
import java.util.UUID;

public record ProgramCreationData(
    String name, EDurationUnit durationUnit, int durationCount, boolean isAPC, List<UUID> skills) {}
