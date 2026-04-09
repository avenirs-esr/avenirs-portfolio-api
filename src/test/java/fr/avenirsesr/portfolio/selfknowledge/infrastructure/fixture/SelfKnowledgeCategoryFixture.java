package fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelfKnowledgeCategoryFixture {
  private UUID id;
  private String title;
  private String description;
  private ESelfKnowledgeCategoryType type;
  private boolean mandatory;
  private Instant createdAt;
  private Instant updatedAt;

  private SelfKnowledgeCategoryFixture() {
    this.id = UUID.randomUUID();
    this.title = "Mes points forts";
    this.description = "Description de test pour la catégorie de connaissance de soi.";
    this.type = ESelfKnowledgeCategoryType.STRENGTHS;
    this.mandatory = true;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static SelfKnowledgeCategoryFixture create() {
    return new SelfKnowledgeCategoryFixture();
  }

  public static List<SelfKnowledgeCategoryFixture> create(int count) {
    List<SelfKnowledgeCategoryFixture> fixtures = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      fixtures.add(create());
    }
    return fixtures;
  }

  public SelfKnowledgeCategoryFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SelfKnowledgeCategoryFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public SelfKnowledgeCategoryFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public SelfKnowledgeCategoryFixture withType(ESelfKnowledgeCategoryType type) {
    this.type = type;
    return this;
  }

  public SelfKnowledgeCategoryFixture withMandatory(boolean mandatory) {
    this.mandatory = mandatory;
    return this;
  }

  public SelfKnowledgeCategoryFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SelfKnowledgeCategoryFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SelfKnowledgeCategory toModel() {
    return SelfKnowledgeCategory.toDomain(
        id, title, description, type, mandatory, createdAt, updatedAt);
  }
}
