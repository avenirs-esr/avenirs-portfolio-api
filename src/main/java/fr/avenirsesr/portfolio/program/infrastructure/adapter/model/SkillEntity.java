package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill")
@NoArgsConstructor
@Getter
@Setter
public class SkillEntity extends AvenirsBaseEntity {
  @OneToMany(
      mappedBy = "skill",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<SkillTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  public SkillEntity(UUID id) {
    this.setId(id);
  }

  public static SkillEntity of(UUID id) {
    return new SkillEntity(id);
  }

  @Override
  public String toString() {
    return "SkillEntity[%s]".formatted(this.getId());
  }
}
