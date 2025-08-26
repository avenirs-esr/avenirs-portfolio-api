package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.Map;

public interface AdditionalSkillConfigurationService {
  AdditionalSkillConfiguration getConfiguration();

  Map<ELanguage, AdditionalSkillConfiguration> getConfigurationWithAllTranslations();

  void postConfiguration(Map<ELanguage, AdditionalSkillConfiguration> configurations);
}
