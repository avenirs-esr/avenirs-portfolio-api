package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillEntity extends AvenirsBaseEntity {
  @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SkillLevelEntity> skillLevels;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_progress_id")
  private ProgramProgressEntity programProgress;

  @OneToMany(
      mappedBy = "skill",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<SkillTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  public SkillEntity(
      UUID id, Set<SkillLevelEntity> skillLevels, ProgramProgressEntity programProgress) {
    this.setId(id);
    this.skillLevels = skillLevels;
    this.programProgress = programProgress;
  }

  @Override
  public String toString() {
    return "SkillEntity[%s]".formatted(this.getId());
  }
}
