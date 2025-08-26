package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"label", "description"})
public record AdditionalSkillLevel(String label, String description) {}
