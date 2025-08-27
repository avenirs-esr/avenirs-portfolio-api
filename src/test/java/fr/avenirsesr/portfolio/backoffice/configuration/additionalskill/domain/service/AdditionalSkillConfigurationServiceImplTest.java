package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.EAdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.input.service.ConfigurationTranslationService;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.UuidV7Generator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdditionalSkillConfigurationServiceImplTest {

  @Spy private UuidGenerator uuidGenerator = new UuidV7Generator();
  @Mock private ConfigurationRepository configurationRepository;
  @Mock private ConfigurationTranslationService configurationTranslationService;

  @InjectMocks private AdditionalSkillConfigurationServiceImpl service;

  @BeforeEach
  void setUp() {
    List<Configuration> mockConfigurations =
        List.of(
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_BEGINNER_LABEL,
                "Débutant"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_BEGINNER_DESCRIPTION,
                "Description débutant"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_INTERMEDIATE_LABEL,
                "Intermédiaire"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_INTERMEDIATE_DESCRIPTION,
                "Description intermédiaire"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_COMPETENT_LABEL,
                "Compétent"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_COMPETENT_DESCRIPTION,
                "Description compétent"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_ADVANCED_LABEL,
                "Avancé"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_ADVANCED_DESCRIPTION,
                "Description avancé"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_EXPERT_LABEL,
                "Expert"),
            Configuration.create(
                uuidGenerator.generate(),
                EConfigurationScope.ADDITIONAL_SKILL,
                EAdditionalSkillConfiguration.LEVEL_EXPERT_DESCRIPTION,
                "Description expert"));

    lenient()
        .when(configurationRepository.inScope(EConfigurationScope.ADDITIONAL_SKILL))
        .thenReturn(mockConfigurations);
  }

  @Test
  void shouldReturnConfigurationFromRepository() {
    // When
    AdditionalSkillConfiguration configuration = service.getConfiguration();

    // Then
    assertNotNull(configuration);
    assertEquals("Débutant", configuration.BEGINNER().label());
    assertEquals("Description débutant", configuration.BEGINNER().description());
    assertEquals("Intermédiaire", configuration.INTERMEDIATE().label());
    assertEquals("Expert", configuration.EXPERT().label());
    verify(configurationRepository).inScope(EConfigurationScope.ADDITIONAL_SKILL);
  }

  @Test
  void shouldSaveNewConfigurationValues() {
    // Given
    AdditionalSkillConfiguration newConfig =
        new AdditionalSkillConfiguration(
            new AdditionalSkillLevel("New Débutant", "New Desc Débutant"),
            new AdditionalSkillLevel("New Intermédiaire", "New Desc Intermédiaire"),
            new AdditionalSkillLevel("New Compétent", "New Desc Compétent"),
            new AdditionalSkillLevel("New Avancé", "New Desc Avancé"),
            new AdditionalSkillLevel("New Expert", "New Desc Expert"));

    // When
    service.postConfiguration(Map.of(ELanguage.FRENCH, newConfig));

    // Then
    verify(configurationTranslationService)
        .buildAndSaveTranslatedEntities(anyMap(), eq(EConfigurationScope.ADDITIONAL_SKILL));
  }

  @Test
  void shouldUpdateExistingConfigurationValues() {
    // Given
    AdditionalSkillConfiguration updatedConfig =
        new AdditionalSkillConfiguration(
            new AdditionalSkillLevel("Débutant modifié", "Description débutant modifiée"),
            new AdditionalSkillLevel("Intermédiaire modifié", "Description intermédiaire modifiée"),
            new AdditionalSkillLevel("Compétent modifié", "Description compétent modifiée"),
            new AdditionalSkillLevel("Avancé modifié", "Description avancé modifiée"),
            new AdditionalSkillLevel("Expert modifié", "Description expert modifiée"));

    // When
    service.postConfiguration(Map.of(ELanguage.FRENCH, updatedConfig));

    // Then
    verify(configurationTranslationService)
        .buildAndSaveTranslatedEntities(anyMap(), eq(EConfigurationScope.ADDITIONAL_SKILL));
  }
}
