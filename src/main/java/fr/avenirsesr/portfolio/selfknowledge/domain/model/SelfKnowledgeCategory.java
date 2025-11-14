package fr.avenirsesr.portfolio.selfknowledge.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfKnowledgeCategory extends AvenirsBaseModel {

  private final String title;
  private final String description;
  private boolean mandatory;

  private SelfKnowledgeCategory(
      UUID id,
      String title,
      String description,
      boolean mandatory,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.title = title;
    this.description = description;
    this.mandatory = mandatory;
  }

  public static SelfKnowledgeCategory create(
      UUID id, String title, String description, boolean mandatory) {
    return new SelfKnowledgeCategory(
        id, title, description, mandatory, Instant.now(), Instant.now());
  }

  public static SelfKnowledgeCategory toDomain(
      UUID id,
      String title,
      String description,
      boolean mandatory,
      Instant createdAt,
      Instant updatedAt) {
    return new SelfKnowledgeCategory(id, title, description, mandatory, createdAt, updatedAt);
  }
}
