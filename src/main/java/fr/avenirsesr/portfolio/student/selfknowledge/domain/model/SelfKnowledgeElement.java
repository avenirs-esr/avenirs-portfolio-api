package fr.avenirsesr.portfolio.student.selfknowledge.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
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
  private final ESelfKnowledgeCategory selfKnowledgeCategory;
  private boolean valorized;

  private SelfKnowledgeElement(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.title = title;
    this.description = description;
    this.rating = rating;
    this.selfKnowledgeCategory = selfKnowledgeCategory;
    this.valorized = valorized;
  }

  public static SelfKnowledgeElement create(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory) {
    return new SelfKnowledgeElement(
        id,
        student,
        title,
        description,
        rating,
        selfKnowledgeCategory,
        false,
        Instant.now(),
        Instant.now());
  }

  public static SelfKnowledgeElement toDomain(
      UUID id,
      Student student,
      String title,
      String description,
      Integer rating,
      ESelfKnowledgeCategory selfKnowledgeCategory,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    return new SelfKnowledgeElement(
        id,
        student,
        title,
        description,
        rating,
        selfKnowledgeCategory,
        valorized,
        createdAt,
        updatedAt);
  }
}
