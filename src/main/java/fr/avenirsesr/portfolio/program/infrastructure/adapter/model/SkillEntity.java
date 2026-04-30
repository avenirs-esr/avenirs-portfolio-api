package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
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
  private Set<SkillTranslationEntity> translations = new HashSet<>();

  public SkillEntity(UUID id, Instant createdAt, Instant updatedAt) {
    this.setId(id);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static SkillEntity of(UUID id, Instant createdAt, Instant updatedAt) {
    return new SkillEntity(id, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "SkillEntity[%s]".formatted(this.getId());
  }
}
