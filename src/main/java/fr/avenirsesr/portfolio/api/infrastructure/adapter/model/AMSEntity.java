package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ams")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AMSEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @ManyToMany
  @JoinTable(
      name = "ams_skill_levels",
      joinColumns = @JoinColumn(name = "ams_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private List<SkillLevelEntity> skillLevels;

  @OneToMany(
      mappedBy = "ams",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<AMSTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  public AMSEntity(UUID id, UserEntity user, Set<SkillLevelEntity> skillLevels) {
    this.id = id;
    this.user = user;
    this.skillLevels = List.copyOf(skillLevels);
  }
}
