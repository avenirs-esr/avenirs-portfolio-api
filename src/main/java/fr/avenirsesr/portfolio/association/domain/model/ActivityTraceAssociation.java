package fr.avenirsesr.portfolio.association.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ActivityTraceAssociation extends AvenirsBaseModel {
  private final DeclaredActivity activity;
  private final Trace trace;

  private ActivityTraceAssociation(
      UUID id, DeclaredActivity activity, Trace trace, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.activity = activity;
    this.trace = trace;
  }

  public static ActivityTraceAssociation create(DeclaredActivity activity, Trace trace) {
    Instant now = Instant.now();
    return new ActivityTraceAssociation(UUID.randomUUID(), activity, trace, now, now);
  }

  public static ActivityTraceAssociation toDomain(
      UUID id, DeclaredActivity activity, Trace trace, Instant createdAt, Instant updatedAt) {
    return new ActivityTraceAssociation(id, activity, trace, createdAt, updatedAt);
  }
}
