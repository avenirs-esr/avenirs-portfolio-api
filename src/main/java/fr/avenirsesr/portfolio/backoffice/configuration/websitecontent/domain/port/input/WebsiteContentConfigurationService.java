package fr.avenirsesr.portfolio.backoffice.configuration.websitecontent.domain.port.input;

import fr.avenirsesr.portfolio.backoffice.configuration.websitecontent.domain.model.BuildLifeProjectConfiguration;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.Map;

public interface WebsiteContentConfigurationService {
  BuildLifeProjectConfiguration getLiefProjectConfiguration();

  Map<ELanguage, BuildLifeProjectConfiguration> getLiefProjectConfigurationWithAllTranslations();

  void postLiefProjectConfiguration(Map<ELanguage, BuildLifeProjectConfiguration> configuration);
}
