package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;

public interface AdditionalSkillConfigurationService {
  AdditionalSkillConfiguration getConfiguration();

  void postConfiguration(AdditionalSkillConfiguration configuration);
}
