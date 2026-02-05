package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

import java.util.List;

public record InstitutionCreationData(
    String institution,
    List<SkillCreationData> skillReferential,
    List<ProgramCreationData> programs) {}
