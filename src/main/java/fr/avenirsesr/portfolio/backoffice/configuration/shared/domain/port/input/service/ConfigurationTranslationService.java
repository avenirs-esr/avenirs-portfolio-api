package fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.input.service;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.List;
import java.util.Map;

public interface ConfigurationTranslationService {
  Map<ELanguage, List<Configuration>> findInScopeWithAllTranslations(EConfigurationScope scope);

  void buildAndSaveTranslatedEntities(
      Map<ELanguage, List<Configuration>> domains, EConfigurationScope scope);
}
