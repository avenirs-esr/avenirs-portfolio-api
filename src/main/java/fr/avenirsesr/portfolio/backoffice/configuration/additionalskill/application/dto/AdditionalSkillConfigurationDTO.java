package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.application.dto;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;

public record AdditionalSkillConfigurationDTO(
    AdditionalSkillLevel BEGINNER,
    AdditionalSkillLevel INTERMEDIATE,
    AdditionalSkillLevel COMPETENT,
    AdditionalSkillLevel ADVANCED,
    AdditionalSkillLevel EXPERT) {
  public static AdditionalSkillConfigurationDTO from(AdditionalSkillConfiguration configuration) {
    return new AdditionalSkillConfigurationDTO(
        configuration.BEGINNER(),
        configuration.INTERMEDIATE(),
        configuration.COMPETENT(),
        configuration.ADVANCED(),
        configuration.EXPERT());
  }
}
