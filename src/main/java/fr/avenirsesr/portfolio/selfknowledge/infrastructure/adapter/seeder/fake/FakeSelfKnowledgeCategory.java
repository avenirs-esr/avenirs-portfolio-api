package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryEntity;
import fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model.SelfKnowledgeCategoryTranslationEntity;
import java.util.Set;
import java.util.UUID;

public class FakeSelfKnowledgeCategory {

  private final SelfKnowledgeCategoryEntity category;

  private FakeSelfKnowledgeCategory(SelfKnowledgeCategoryEntity category) {
    this.category = category;
  }

  public static FakeSelfKnowledgeCategory of(
      String frTitle, String frDescription, ESelfKnowledgeCategoryType type, boolean mandatory) {

    var entity = SelfKnowledgeCategoryEntity.of(UUID.randomUUID(), type, mandatory);

    var frTranslation =
        SelfKnowledgeCategoryTranslationEntity.of(
            UUID.randomUUID(), ELanguage.FRENCH, frTitle, frDescription, entity);

    entity.setTranslations(Set.of(frTranslation));

    return new FakeSelfKnowledgeCategory(entity);
  }

  public SelfKnowledgeCategoryEntity toEntity() {
    return category;
  }
}
