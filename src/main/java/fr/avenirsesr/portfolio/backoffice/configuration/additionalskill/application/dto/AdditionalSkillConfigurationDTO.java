package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.application.dto;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(requiredProperties = {"BEGINNER", "INTERMEDIATE", "COMPETENT", "ADVANCED", "EXPERT"})
public record AdditionalSkillConfigurationDTO(
    AdditionalSkillLevel BEGINNER,
    AdditionalSkillLevel INTERMEDIATE,
    AdditionalSkillLevel COMPETENT,
    AdditionalSkillLevel ADVANCED,
    AdditionalSkillLevel EXPERT) {

  public static AdditionalSkillConfigurationDTO fromModel(
      AdditionalSkillConfiguration configuration) {
    return new AdditionalSkillConfigurationDTO(
        configuration.BEGINNER(),
        configuration.INTERMEDIATE(),
        configuration.COMPETENT(),
        configuration.ADVANCED(),
        configuration.EXPERT());
  }

  public static AdditionalSkillConfiguration toModel(AdditionalSkillConfigurationDTO dto) {
    return new AdditionalSkillConfiguration(
        new AdditionalSkillLevel(dto.BEGINNER().label(), dto.BEGINNER().description()),
        new AdditionalSkillLevel(dto.INTERMEDIATE().label(), dto.INTERMEDIATE().description()),
        new AdditionalSkillLevel(dto.COMPETENT().label(), dto.COMPETENT().description()),
        new AdditionalSkillLevel(dto.ADVANCED().label(), dto.ADVANCED().description()),
        new AdditionalSkillLevel(dto.EXPERT().label(), dto.EXPERT().description()));
  }
}
