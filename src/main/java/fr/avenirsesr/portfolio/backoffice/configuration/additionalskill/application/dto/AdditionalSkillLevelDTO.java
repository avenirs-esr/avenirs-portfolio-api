package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.application.dto;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;

public record AdditionalSkillLevelDTO(String label, String description) {
  public static AdditionalSkillLevelDTO from(AdditionalSkillLevel level) {
    return new AdditionalSkillLevelDTO(level.label(), level.description());
  }
}
