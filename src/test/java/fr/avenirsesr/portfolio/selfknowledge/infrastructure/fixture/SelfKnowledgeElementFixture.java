package fr.avenirsesr.portfolio.selfknowledge.infrastructure.fixture;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeCategory;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.StudentFixture;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelfKnowledgeElementFixture {
  private UUID id;
  private Student student;
  private String title;
  private String description;
  private Integer rating;
  private SelfKnowledgeCategory selfKnowledgeCategory;
  private Instant createdAt;
  private Instant updatedAt;

  private SelfKnowledgeElementFixture() {
    this.id = UUID.randomUUID();
    this.student = StudentFixture.create().toModel();
    this.title = "Créativité";
    this.description =
        "J’ai une approche originale des problèmes et j’aime trouver des solutions innovantes dans"
            + " mes projets scolaires et personnels.";
    this.rating = 1;
    this.selfKnowledgeCategory = SelfKnowledgeCategoryFixture.create().toModel();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static SelfKnowledgeElementFixture create() {
    return new SelfKnowledgeElementFixture();
  }

  public static List<SelfKnowledgeElementFixture> create(int count) {
    List<SelfKnowledgeElementFixture> fixtures = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      fixtures.add(create());
    }
    return fixtures;
  }

  public SelfKnowledgeElementFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SelfKnowledgeElementFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public SelfKnowledgeElementFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public SelfKnowledgeElementFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public SelfKnowledgeElementFixture withRating(Integer rating) {
    this.rating = rating;
    return this;
  }

  public SelfKnowledgeElementFixture withSelfKnowledgeCategory(
      SelfKnowledgeCategory selfKnowledgeCategory) {
    this.selfKnowledgeCategory = selfKnowledgeCategory;
    return this;
  }

  public SelfKnowledgeElementFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SelfKnowledgeElementFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SelfKnowledgeElement toModel() {
    return SelfKnowledgeElement.toDomain(
        id, student, title, description, rating, selfKnowledgeCategory, createdAt, updatedAt);
  }
}
