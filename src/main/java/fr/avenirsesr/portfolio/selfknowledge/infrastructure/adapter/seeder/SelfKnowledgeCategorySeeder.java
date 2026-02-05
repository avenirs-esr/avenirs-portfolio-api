package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestData;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data.SelfKnowledgeCategoryCreationData;
import fr.avenirsesr.portfolio.shared.infrastructure.utils.FileReader;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelfKnowledgeCategorySeeder {
  private static final String PATH_FILE_FR = "seeder/self-knowledge-categories.fr.json";
  private static final String PATH_FILE_EN = "seeder/self-knowledge-categories.en.json";
  private static final String PATH_FILE_ES = "seeder/self-knowledge-categories.es.json";
  private final FileReader fileReader;
  private final SelfKnowledgeService selfKnowledgeService;

  @Transactional
  public List<SelfKnowledgeCategoryEntity> seed() {
    log.info("Seeding self-knowledge categories from file (FR/EN/ES)...");

    List<SelfKnowledgeCategoryCreationData> creationDataFR =
        fileReader.readJSON(PATH_FILE_FR, new TypeReference<>() {});
    List<SelfKnowledgeCategoryCreationData> creationDataEN =
        fileReader.readJSON(PATH_FILE_EN, new TypeReference<>() {});
    List<SelfKnowledgeCategoryCreationData> creationDataES =
        fileReader.readJSON(PATH_FILE_ES, new TypeReference<>() {});

    List<SelfKnowledgeCategory> categories = new ArrayList<>();
    creationDataFR.forEach(
        data -> {
          RequestContext.set(new RequestData(Optional.empty(), ELanguage.FRENCH));
          var category =
              selfKnowledgeService.createSelfKnowledgeCategory(
                  data.id(), data.title(), data.description(), data.type(), data.isMandatory());

          creationDataEN.stream()
              .filter(c -> c.id().equals(data.id()))
              .findFirst()
              .ifPresent(
                  en -> {
                    RequestContext.set(new RequestData(Optional.empty(), ELanguage.ENGLISH));
                    selfKnowledgeService.updateSelfKnowledgeCategory(
                        en.id(), en.title(), en.description());
                  });

          creationDataES.stream()
              .filter(c -> c.id().equals(data.id()))
              .findFirst()
              .ifPresent(
                  es -> {
                    RequestContext.set(new RequestData(Optional.empty(), ELanguage.SPANISH));
                    selfKnowledgeService.updateSelfKnowledgeCategory(
                        es.id(), es.title(), es.description());
                  });
          categories.add(category);
        });

    return categories.stream().map(SelfKnowledgeCategoryMapper.INSTANCE::fromDomain).toList();
  }
}
