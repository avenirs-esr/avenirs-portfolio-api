package fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.EAdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.mapper.ConfigurationMapper;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.model.ConfigurationEntity;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.model.ConfigurationTranslationEntity;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.infrastructure.adapter.repository.ConfigurationDatabaseRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ConfigurationTranslationServiceImplTest {

  private ConfigurationDatabaseRepository repository;
  private ConfigurationTranslationServiceImpl service;

  private ConfigurationEntity entityFr;

  @BeforeEach
  void setUp() {
    repository = mock(ConfigurationDatabaseRepository.class);
    service = new ConfigurationTranslationServiceImpl(repository);

    // Entité de base avec traduction FR
    entityFr = new ConfigurationEntity();
    entityFr.setId(UUID.randomUUID());
    entityFr.setScope(EConfigurationScope.ADDITIONAL_SKILL);
    entityFr.setKey("LEVEL_BEGINNER_LABEL");
    entityFr.setValue("Débutant");

    ConfigurationTranslationEntity translationFr =
        ConfigurationTranslationEntity.of(
            UUID.randomUUID(), ELanguage.FRENCH, entityFr, "Débutant");
    entityFr.setTranslations(Set.of(translationFr));
  }

  @Test
  void shouldReturnEmptyMapWhenNoEntities() {
    when(repository.inScopeEntities(EConfigurationScope.ADDITIONAL_SKILL)).thenReturn(List.of());

    var result = service.findInScopeWithAllTranslations(EConfigurationScope.ADDITIONAL_SKILL);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnConfigurationsGroupedByLanguage() {
    when(repository.inScopeEntities(EConfigurationScope.ADDITIONAL_SKILL))
        .thenReturn(List.of(entityFr));

    var result = service.findInScopeWithAllTranslations(EConfigurationScope.ADDITIONAL_SKILL);

    assertTrue(result.containsKey(ELanguage.FRENCH));
    assertEquals(1, result.get(ELanguage.FRENCH).size());
    assertEquals("Débutant", result.get(ELanguage.FRENCH).get(0).getValue());
  }

  @Test
  void shouldSaveNewEntityWhenNotExists() {
    // given
    Configuration configFr =
        Configuration.create(
            UUID.randomUUID(),
            EConfigurationScope.ADDITIONAL_SKILL,
            EAdditionalSkillConfiguration.LEVEL_BEGINNER_LABEL,
            "Débutant");
    Map<ELanguage, List<Configuration>> input = Map.of(ELanguage.FRENCH, List.of(configFr));

    when(repository.inScopeEntities(EConfigurationScope.ADDITIONAL_SKILL)).thenReturn(List.of());

    // when
    service.buildAndSaveTranslatedEntities(input, EConfigurationScope.ADDITIONAL_SKILL);

    // then
    ArgumentCaptor<List<ConfigurationEntity>> captor = ArgumentCaptor.forClass(List.class);
    verify(repository).saveAllEntities(captor.capture());

    var saved = captor.getValue().get(0);
    assertEquals(EAdditionalSkillConfiguration.LEVEL_BEGINNER_LABEL.name(), saved.getKey());
    assertEquals("Débutant", saved.getTranslations().stream().findFirst().orElseThrow().getValue());
  }

  @Test
  void shouldUpdateExistingEntityValueForLanguage() {
    // given
    Configuration configFrUpdated =
        Configuration.create(
            entityFr.getId(),
            EConfigurationScope.ADDITIONAL_SKILL,
            EAdditionalSkillConfiguration.LEVEL_BEGINNER_LABEL,
            "Débutant modifié");

    Map<ELanguage, List<Configuration>> input = Map.of(ELanguage.FRENCH, List.of(configFrUpdated));

    when(repository.inScopeEntities(EConfigurationScope.ADDITIONAL_SKILL))
        .thenReturn(List.of(entityFr));

    // when
    service.buildAndSaveTranslatedEntities(input, EConfigurationScope.ADDITIONAL_SKILL);

    // then
    assertEquals(
        "Débutant modifié",
        entityFr.getTranslations().stream().findFirst().orElseThrow().getValue());
    verify(repository).saveAllEntities(anyList());
  }

  @Test
  void shouldAddNewTranslationForAnotherLanguage() {
    // given
    Configuration configFr = ConfigurationMapper.toDomain(entityFr);
    Configuration configEn =
        Configuration.create(
            entityFr.getId(),
            EConfigurationScope.ADDITIONAL_SKILL,
            EAdditionalSkillConfiguration.LEVEL_BEGINNER_LABEL,
            "Beginner");

    Map<ELanguage, List<Configuration>> input =
        Map.of(
            ELanguage.FRENCH, List.of(configFr),
            ELanguage.ENGLISH, List.of(configEn));

    when(repository.inScopeEntities(EConfigurationScope.ADDITIONAL_SKILL))
        .thenReturn(List.of(entityFr));

    // when
    service.buildAndSaveTranslatedEntities(input, EConfigurationScope.ADDITIONAL_SKILL);

    // then
    assertTrue(
        entityFr.getTranslations().stream().anyMatch(t -> t.getLanguage() == ELanguage.ENGLISH));
    verify(repository).saveAllEntities(anyList());
  }

  @Test
  void shouldThrowWhenMissingTranslationForLanguage() {
    Configuration configFr = ConfigurationMapper.toDomain(entityFr);
    Map<ELanguage, List<Configuration>> input =
        Map.of(
            ELanguage.FRENCH, List.of(configFr),
            ELanguage.ENGLISH, List.of() // manquant
            );

    when(repository.inScopeEntities(EConfigurationScope.ADDITIONAL_SKILL))
        .thenReturn(List.of(entityFr));

    assertThrows(
        NoSuchElementException.class,
        () -> service.buildAndSaveTranslatedEntities(input, EConfigurationScope.ADDITIONAL_SKILL));
  }
}
