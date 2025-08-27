package fr.avenirsesr.portfolio.backoffice.configuration.websitecontent.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.input.service.ConfigurationTranslationService;
import fr.avenirsesr.portfolio.backoffice.configuration.websitecontent.domain.model.BuildLifeProjectConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.websitecontent.domain.model.EWebsiteContentConfiguration;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WebsiteContentConfigurationServiceImplTest {

  @Mock private ConfigurationTranslationService configurationTranslationService;

  @InjectMocks private WebsiteContentConfigurationServiceImpl service;

  @Test
  void shouldReturnConfigurationByLanguage() {
    // Given
    Configuration configFr =
        Configuration.create(
            UUID.randomUUID(),
            EConfigurationScope.WEBSITE_CONTENT,
            EWebsiteContentConfiguration.BUILD_LIFE_PROJECT_CONTENT,
            "<p>Projet de vie</p>");

    Configuration configEn =
        Configuration.create(
            UUID.randomUUID(),
            EConfigurationScope.WEBSITE_CONTENT,
            EWebsiteContentConfiguration.BUILD_LIFE_PROJECT_CONTENT,
            "<p>Life project</p>");

    when(configurationTranslationService.findInScopeWithAllTranslations(
            EConfigurationScope.WEBSITE_CONTENT))
        .thenReturn(
            Map.of(
                ELanguage.FRENCH, List.of(configFr),
                ELanguage.ENGLISH, List.of(configEn)));

    // When
    Map<ELanguage, BuildLifeProjectConfiguration> result =
        service.getLiefProjectConfigurationWithAllTranslations();

    // Then
    assertNotNull(result);
    assertEquals("<p>Projet de vie</p>", result.get(ELanguage.FRENCH).html());
    assertEquals("<p>Life project</p>", result.get(ELanguage.ENGLISH).html());
    verify(configurationTranslationService)
        .findInScopeWithAllTranslations(EConfigurationScope.WEBSITE_CONTENT);
  }

  @Test
  void shouldSaveNewConfigurations() {
    // Given
    BuildLifeProjectConfiguration config =
        new BuildLifeProjectConfiguration("<p>Nouveau contenu</p>");

    when(configurationTranslationService.findInScopeWithAllTranslations(
            EConfigurationScope.WEBSITE_CONTENT))
        .thenReturn(Map.of(ELanguage.FRENCH, List.of()));

    // When
    service.postLiefProjectConfiguration(Map.of(ELanguage.FRENCH, config));

    // Then
    verify(configurationTranslationService)
        .buildAndSaveTranslatedEntities(anyMap(), eq(EConfigurationScope.WEBSITE_CONTENT));
  }

  @Test
  void shouldUpdateExistingConfiguration() {
    // Given
    Configuration existing =
        Configuration.create(
            UUID.randomUUID(),
            EConfigurationScope.WEBSITE_CONTENT,
            EWebsiteContentConfiguration.BUILD_LIFE_PROJECT_CONTENT,
            "<p>Ancien contenu</p>");

    BuildLifeProjectConfiguration updated =
        new BuildLifeProjectConfiguration("<p>Contenu mis à jour</p>");

    when(configurationTranslationService.findInScopeWithAllTranslations(
            EConfigurationScope.WEBSITE_CONTENT))
        .thenReturn(Map.of(ELanguage.FRENCH, List.of(existing)));

    // When
    service.postLiefProjectConfiguration(Map.of(ELanguage.FRENCH, updated));

    // Then
    verify(configurationTranslationService)
        .buildAndSaveTranslatedEntities(anyMap(), eq(EConfigurationScope.WEBSITE_CONTENT));
    assertEquals("<p>Contenu mis à jour</p>", existing.getValue());
  }
}
