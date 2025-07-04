package fr.avenirsesr.portfolio.ams.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cohort")
@NoArgsConstructor
@Getter
@Setter
public class CohortEntity extends AvenirsBaseEntity {

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
  private TrainingPathEntity programProgress;

  @ManyToMany(mappedBy = "cohorts")
  private Set<AMSEntity> amsEntities;

  private CohortEntity(
      UUID id,
      String name,
      String description,
      Set<UserEntity> users,
      TrainingPathEntity programProgress,
      Set<AMSEntity> amsEntities) {
    this.setId(id);
    this.name = name;
    this.description = description;
    this.users = users;
    this.programProgress = programProgress;
    this.amsEntities = amsEntities;
  }

  public static CohortEntity of(
      UUID id,
      String name,
      String description,
      Set<UserEntity> users,
      ProgramProgressEntity programProgress,
      Set<AMSEntity> amsEntities) {
    return new CohortEntity(id, name, description, users, programProgress, amsEntities);
  }
}
