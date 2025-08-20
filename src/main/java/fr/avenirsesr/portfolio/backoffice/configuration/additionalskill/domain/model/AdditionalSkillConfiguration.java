package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model;

public record AdditionalSkillConfiguration(
    AdditionalSkillLevel BEGINNER,
    AdditionalSkillLevel INTERMEDIATE,
    AdditionalSkillLevel COMPETENT,
    AdditionalSkillLevel ADVANCED,
    AdditionalSkillLevel EXPERT) {}
