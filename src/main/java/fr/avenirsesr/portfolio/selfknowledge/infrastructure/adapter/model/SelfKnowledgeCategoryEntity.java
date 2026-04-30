package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.TranslatableEntity;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "self_knowledge_category",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_self_knowledge_category_type",
          columnNames = {"type"})
    })
@NoArgsConstructor
@Getter
@Setter
public class SelfKnowledgeCategoryEntity
    extends TranslatableEntity<SelfKnowledgeCategoryTranslationEntity> {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ESelfKnowledgeCategoryType type;

  @Column(nullable = false)
  private boolean mandatory;

  @OneToMany(
      mappedBy = "category",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<SelfKnowledgeCategoryTranslationEntity> translations = new HashSet<>();

  @ManyToMany(mappedBy = "selfKnowledgeCategories")
  private Set<StudentEntity> students = new HashSet<>();

  private SelfKnowledgeCategoryEntity(
      UUID id,
      ESelfKnowledgeCategoryType type,
      boolean mandatory,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.type = type;
    this.mandatory = mandatory;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static SelfKnowledgeCategoryEntity of(
      UUID id,
      ESelfKnowledgeCategoryType type,
      boolean mandatory,
      Instant createdAt,
      Instant updatedAt) {
    return new SelfKnowledgeCategoryEntity(id, type, mandatory, createdAt, updatedAt);
  }
}
