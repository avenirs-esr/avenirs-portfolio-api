package fr.avenirsesr.portfolio.trace.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.StudentProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "trace")
@NoArgsConstructor
@Getter
@Setter
public class TraceEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ELanguage language;

  @ManyToOne(optional = true)
  private StudentProgressEntity studentProgress;

  @ManyToMany
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinTable(
      name = "trace_ams",
      joinColumns = @JoinColumn(name = "trace_id"),
      inverseJoinColumns = @JoinColumn(name = "ams_id"))
  private List<AMSEntity> amses;

  @Column(nullable = false)
  private boolean isGroup;

  @Column private Instant deletedAt;

  private TraceEntity(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      StudentProgressEntity studentProgress,
      List<AMSEntity> amses,
      boolean isGroup,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    this.setId(id);
    this.user = user;
    this.title = title;
    this.language = language;
    this.studentProgress = studentProgress;
    this.amses = amses;
    this.isGroup = isGroup;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.setDeletedAt(deletedAt);
  }

  public static TraceEntity of(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      StudentProgressEntity studentProgress,
      List<AMSEntity> amses,
      boolean isGroup,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    return new TraceEntity(
        id,
        user,
        title,
        language,
        studentProgress,
        amses,
        isGroup,
        createdAt,
        updatedAt,
        deletedAt);
  }
}
