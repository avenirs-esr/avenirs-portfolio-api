package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cohort")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CohortEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(length = 255)
  private String description;

  @ManyToMany
  @JoinTable(
      name = "cohort_user",
      joinColumns = @JoinColumn(name = "cohort_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<UserEntity> users;

  @ManyToOne(optional = false)
  @JoinColumn(name = "program_progress_id")
  private ProgramProgressEntity programProgress;

  @ManyToMany
  @JoinTable(
      name = "cohort_ams",
      joinColumns = @JoinColumn(name = "cohort_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private Set<AMSEntity> amsEntities;
}
