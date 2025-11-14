package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "self_knowledge_category_translation")
@NoArgsConstructor
@Getter
@Setter
public class SelfKnowledgeCategoryTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
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
  }

  public static SelfKnowledgeCategoryTranslationEntity of(
      UUID id,
      ELanguage language,
      String title,
      String description,
      SelfKnowledgeCategoryEntity category) {
    return new SelfKnowledgeCategoryTranslationEntity(id, language, title, description, category);
  }
}
