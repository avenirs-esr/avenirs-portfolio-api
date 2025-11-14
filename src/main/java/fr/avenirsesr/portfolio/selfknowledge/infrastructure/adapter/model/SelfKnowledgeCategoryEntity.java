package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "self_knowledge_category")
@NoArgsConstructor
@Getter
@Setter
public class SelfKnowledgeCategoryEntity extends AvenirsBaseEntity {

  @Column(nullable = false)
  private boolean mandatory;

  @OneToMany(
      mappedBy = "category",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<SelfKnowledgeCategoryTranslationEntity> translations = new HashSet<>();

  private SelfKnowledgeCategoryEntity(UUID id, boolean mandatory) {
    this.setId(id);
    this.mandatory = mandatory;
  }

  public static SelfKnowledgeCategoryEntity of(UUID id, boolean mandatory) {
    return new SelfKnowledgeCategoryEntity(id, mandatory);
  }
}
