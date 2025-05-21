package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
public class SkillEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @OneToMany
  @JoinTable(
      name = "skill_skill_levels",
      joinColumns = @JoinColumn(name = "skill_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private Set<SkillLevelEntity> skillLevels;

  public static SkillEntity fromDomain(Skill skill) {
    return new SkillEntity(
        skill.getId(),
        skill.getName(),
        skill.getSkillLevels().stream()
            .map(SkillLevelEntity::fromDomain)
            .collect(Collectors.toSet()));
  }

  public static Skill toDomain(SkillEntity skillEntity) {
    return Skill.toDomain(
            skillEntity.getId(),
            skillEntity.getName(),
            skillEntity.getSkillLevels().stream()
                    .map(SkillLevelEntity::toDomain)
                    .collect(Collectors.toSet()));
  }
}
