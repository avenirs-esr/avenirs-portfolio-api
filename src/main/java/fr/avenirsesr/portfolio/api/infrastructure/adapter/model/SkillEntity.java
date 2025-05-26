package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.*;
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
public class SkillEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SkillLevelEntity> skillLevels;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_progress_id")
  private ProgramProgressEntity programProgress;

  @Override
  public String toString() {
    return "SkillEntity[%s]".formatted(id);
  }
}
