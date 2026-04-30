package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.DESCRIPTION_LENGTH;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.error.domain.exception.WrongClassTypeArgumentException;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.model.TranslationEntity;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "self_knowledge_category_translation",
    indexes = {
      @Index(name = "idx_sk_category_tr_category", columnList = "category_id"),
      @Index(name = "idx_sk_category_tr_category_lang", columnList = "category_id, language")
    })
@NoArgsConstructor
@Getter
@Setter
public class SelfKnowledgeCategoryTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT", length = DESCRIPTION_LENGTH)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private SelfKnowledgeCategoryEntity category;

  private SelfKnowledgeCategoryTranslationEntity(
      UUID id,
      ELanguage language,
      String title,
      String description,
      SelfKnowledgeCategoryEntity category) {
    this.setId(id);
    this.language = language;
    this.title = title;
    this.description = description;
    this.category = category;
    this.setCreatedAt(category.getCreatedAt());
    this.setUpdatedAt(category.getUpdatedAt());
  }

  public static SelfKnowledgeCategoryTranslationEntity of(
      UUID id,
      ELanguage language,
      String title,
      String description,
      SelfKnowledgeCategoryEntity category) {
    return new SelfKnowledgeCategoryTranslationEntity(id, language, title, description, category);
  }

  public static SelfKnowledgeCategoryTranslationEntity create(
      AvenirsBaseModel model, AvenirsBaseEntity baseEntity, ELanguage language) {
    if (!(model instanceof SelfKnowledgeCategory domain)
        || !(baseEntity instanceof SelfKnowledgeCategoryEntity entity)) {
      throw new WrongClassTypeArgumentException(
          "domain and entity should be of type SelfKnowledgeCategory");
    }
    return of(UUID.randomUUID(), language, domain.getTitle(), domain.getDescription(), entity);
  }

  public static SelfKnowledgeCategoryTranslationEntity update(
      TranslationEntity translation, AvenirsBaseModel model) {
    if (!(model instanceof SelfKnowledgeCategory domain)
        || !(translation instanceof SelfKnowledgeCategoryTranslationEntity translationEntity)) {
      throw new WrongClassTypeArgumentException(
          "domain and translation should be of type SelfKnowledgeCategory");
    }
    translationEntity.setTitle(domain.getTitle());
    translationEntity.setDescription(domain.getDescription());
    return translationEntity;
  }
}
