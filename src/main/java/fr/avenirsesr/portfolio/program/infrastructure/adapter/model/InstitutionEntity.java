package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.TranslatableEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution")
@NoArgsConstructor
@Getter
@Setter
public class InstitutionEntity extends TranslatableEntity<InstitutionTranslationEntity> {
  @OneToMany(
      mappedBy = "institution",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<InstitutionTranslationEntity> translations = new HashSet<>();

  private InstitutionEntity(UUID id) {
    this.setId(id);
  }

  public static InstitutionEntity of(UUID id) {
    return new InstitutionEntity(id);
  }
}
