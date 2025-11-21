package fr.avenirsesr.portfolio.selfknowledge.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfKnowledgeElement extends AvenirsBaseModel {
  private final Student student;
  private String title;
  private String description;
  private Integer rating;
  private final SelfKnowledgeCategory selfKnowledgeCategory;

  private SelfKnowledgeElement(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      SelfKnowledgeCategory selfKnowledgeCategory,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.title = title;
    this.description = description;
    this.rating = rating;
    this.selfKnowledgeCategory = selfKnowledgeCategory;
  }

  public static SelfKnowledgeElement create(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      SelfKnowledgeCategory selfKnowledgeCategory) {
    return new SelfKnowledgeElement(
        id,
        student,
        title,
        description,
        rating,
        selfKnowledgeCategory,
        Instant.now(),
        Instant.now());
  }

  public static SelfKnowledgeElement toDomain(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      SelfKnowledgeCategory selfKnowledgeCategory,
      Instant createdAt,
      Instant updatedAt) {
    return new SelfKnowledgeElement(
        id, student, title, description, rating, selfKnowledgeCategory, createdAt, updatedAt);
  }
}
