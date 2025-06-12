package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.util.List;
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
  
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;
  
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @ManyToMany
  @JoinTable(
      name = "ams_skill_levels",
      joinColumns = @JoinColumn(name = "ams_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_level_id"))
  private List<SkillLevelEntity> skillLevels;
}
