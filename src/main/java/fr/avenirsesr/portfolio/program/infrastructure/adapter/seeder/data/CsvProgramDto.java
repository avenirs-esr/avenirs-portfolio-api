package fr.avenirsesr.portfolio.program.infrastructure.adapter.seeder.data;

public record CsvProgramDto(
    String university,
    String program,
    String skill,
    String skillLevelName,
    String SkillLevelDescription) {}
