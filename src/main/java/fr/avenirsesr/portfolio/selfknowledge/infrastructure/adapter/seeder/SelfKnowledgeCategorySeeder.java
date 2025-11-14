package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.repository.SelfKnowledgeCategoryDatabaseRepository;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data.CsvSelfKnowledgeCategoryDto;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data.SelfKnowledgeCategoryCSVData;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelfKnowledgeCategorySeeder {

  private final SelfKnowledgeCategoryDatabaseRepository repository;

  @Transactional
  public List<SelfKnowledgeCategoryEntity> seed() {
    log.info("Seeding self-knowledge categories from CSV (FR/EN/ES)...");

    Map<ELanguage, List<CsvSelfKnowledgeCategoryDto>> dataByLang =
        SelfKnowledgeCategoryCSVData.getAllByLanguage();

    List<CsvSelfKnowledgeCategoryDto> frRows = dataByLang.get(ELanguage.FRENCH);
    List<CsvSelfKnowledgeCategoryDto> enRows = dataByLang.get(ELanguage.ENGLISH);
    List<CsvSelfKnowledgeCategoryDto> esRows = dataByLang.get(ELanguage.SPANISH);

    if (frRows.size() != enRows.size() || frRows.size() != esRows.size()) {
      throw new IllegalStateException(
          "CSV self-knowledge categories are misaligned between languages.");
    }

    List<SelfKnowledgeCategoryEntity> entities = new ArrayList<>();

    for (int i = 0; i < frRows.size(); i++) {
      CsvSelfKnowledgeCategoryDto fr = frRows.get(i);
      CsvSelfKnowledgeCategoryDto en = enRows.get(i);
      CsvSelfKnowledgeCategoryDto es = esRows.get(i);

      var categoryEntity = SelfKnowledgeCategoryEntity.of(UUID.randomUUID(), fr.mandatory());

      Set<SelfKnowledgeCategoryTranslationEntity> translations = new HashSet<>();

      translations.add(
          SelfKnowledgeCategoryTranslationEntity.of(
              UUID.randomUUID(), ELanguage.FRENCH, fr.title(), fr.description(), categoryEntity));

      translations.add(
          SelfKnowledgeCategoryTranslationEntity.of(
              UUID.randomUUID(), ELanguage.ENGLISH, en.title(), en.description(), categoryEntity));

      translations.add(
          SelfKnowledgeCategoryTranslationEntity.of(
              UUID.randomUUID(), ELanguage.SPANISH, es.title(), es.description(), categoryEntity));

      categoryEntity.setTranslations(translations);

      entities.add(categoryEntity);
    }

    repository.saveAllEntities(entities);

    long mandatoryCount =
        entities.stream().filter(SelfKnowledgeCategoryEntity::isMandatory).count();
    log.info(
        "✔ {} self-knowledge categories created ({} mandatory, {} optional)",
        entities.size(),
        mandatoryCount,
        entities.size() - mandatoryCount);

    return entities;
  }
}
