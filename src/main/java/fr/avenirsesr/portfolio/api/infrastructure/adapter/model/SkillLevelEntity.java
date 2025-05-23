package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column
  @Enumerated(EnumType.STRING)
  private ESkillLevelStatus status;

  @ManyToMany
  @JoinTable(
      name = "track_skill_levels",
      joinColumns = @JoinColumn(name = "track_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private List<TrackEntity> tracks;

  @ManyToMany
  @JoinTable(
      name = "skill_level_ams",
      joinColumns = @JoinColumn(name = "skill_level_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private List<AMSEntity> amses;
}
