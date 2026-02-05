package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

import java.util.List;
import java.util.UUID;

public record SkillCreationData(UUID id, String name, List<SkillLevelCreationData> levels) {}
