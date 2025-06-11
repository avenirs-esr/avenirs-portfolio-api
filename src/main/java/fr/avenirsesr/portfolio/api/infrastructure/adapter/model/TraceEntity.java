package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trace")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TraceEntity {
  @Id private UUID id;

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private ELanguage language;

  @ManyToMany
  @JoinTable(
      name = "trace_skill_levels",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private List<SkillLevelEntity> skillLevels;

  @ManyToMany
  @JoinTable(
      name = "trace_ams",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private List<AMSEntity> amses;

  @Column(nullable = false)
  private boolean isGroup;

  @Column(nullable = false)
  private Instant createdAt;
}
