package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

import java.util.List;
import java.util.UUID;

public record InstitutionCreationData(
    UUID id,
    String institution,
    List<SkillCreationData> skillReferential,
    List<ProgramCreationData> programs) {}
