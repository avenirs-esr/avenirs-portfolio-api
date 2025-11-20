package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "self_knowledge_element")
@NoArgsConstructor
@Getter
@Setter
public class SelfKnowledgeElementEntity extends AvenirsBaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private StudentEntity student;

  @Column(nullable = false, length = 80)
  private String title;

  @Column(nullable = false, length = 400)
  private String description;

  @Column(nullable = true)
  @Min(1)
  @Max(5)
  private Integer rating;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "self_knowledge_category_id", nullable = false)
  private SelfKnowledgeCategoryEntity selfKnowledgeCategory;

  private SelfKnowledgeElementEntity(
      UUID id,
      StudentEntity student,
      String title,
      String description,
      Integer rating,
      SelfKnowledgeCategoryEntity selfKnowledgeCategory) {
    this.setId(id);
    this.student = student;
    this.title = title;
    this.description = description;
    this.rating = rating;
    this.selfKnowledgeCategory = selfKnowledgeCategory;
  }

  public static SelfKnowledgeElementEntity of(
      UUID id,
      StudentEntity student,
      String title,
      String description,
      Integer rating,
      SelfKnowledgeCategoryEntity selfKnowledgeCategory) {
    return new SelfKnowledgeElementEntity(
        id, student, title, description, rating, selfKnowledgeCategory);
  }
}
