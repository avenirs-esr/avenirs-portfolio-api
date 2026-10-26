package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;

public record DeclaredActivityAssessment(
    String comment,
    Integer grade,
    Boolean validation,
    Instant requestedAt,
    Instant assessedAt,
    Staff assessedBy) {

  public static DeclaredActivityAssessment requested(Instant requestedAt) {
    return new DeclaredActivityAssessment(null, null, null, requestedAt, null, null);
  }
}
