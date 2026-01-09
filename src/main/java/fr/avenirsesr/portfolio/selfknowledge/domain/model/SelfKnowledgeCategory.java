package fr.avenirsesr.portfolio.selfknowledge.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfKnowledgeCategory extends AvenirsBaseModel {

  private String title;
  private String description;
  private final ESelfKnowledgeCategoryType type;
  private final boolean mandatory;

  private SelfKnowledgeCategory(
      UUID id,
      String title,
      String description,
      ESelfKnowledgeCategoryType type,
      boolean mandatory,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.title = title;
    this.description = description;
    this.type = type;
    this.mandatory = mandatory;
  }

  public static SelfKnowledgeCategory create(
      UUID id,
      String title,
      String description,
      ESelfKnowledgeCategoryType type,
      boolean mandatory) {
    return new SelfKnowledgeCategory(
        id, title, description, type, mandatory, Instant.now(), Instant.now());
  }

  public static SelfKnowledgeCategory toDomain(
      UUID id,
      String title,
      String description,
      ESelfKnowledgeCategoryType type,
      boolean mandatory,
      Instant createdAt,
      Instant updatedAt) {
    return new SelfKnowledgeCategory(id, title, description, type, mandatory, createdAt, updatedAt);
  }
}
