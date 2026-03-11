package fr.avenirsesr.portfolio.association.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity_trace_association",
    uniqueConstraints = @UniqueConstraint(columnNames = {"activity_id", "trace_id"}))
@NoArgsConstructor
@Getter
@Setter
public class ActivityTraceAssociationEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "activity_id")
  private DeclaredActivityEntity activity;

  @ManyToOne(optional = false)
  @JoinColumn(name = "trace_id")
  private TraceEntity trace;

  private ActivityTraceAssociationEntity(
      UUID id,
      DeclaredActivityEntity activity,
      TraceEntity trace,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.activity = activity;
    this.trace = trace;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ActivityTraceAssociationEntity of(
      UUID id,
      DeclaredActivityEntity activity,
      TraceEntity trace,
      Instant createdAt,
      Instant updatedAt) {
    return new ActivityTraceAssociationEntity(id, activity, trace, createdAt, updatedAt);
  }
}
