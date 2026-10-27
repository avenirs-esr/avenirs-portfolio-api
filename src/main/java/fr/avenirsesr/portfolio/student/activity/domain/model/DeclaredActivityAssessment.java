package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;

public record DeclaredActivityAssessment(
    String comment,
    Integer grade,
    Boolean validation,
    Instant requestedAt,
    Instant assessedAt,
    Staff assessedBy,
    Instant modifiedAt,
    Instant consultedAt) {

  public static DeclaredActivityAssessment requested(Instant requestedAt) {
    return new DeclaredActivityAssessment(null, null, null, requestedAt, null, null, null, null);
  }

  public boolean isSent() {
    return assessedAt != null;
  }

  public DeclaredActivityAssessment send(
      String comment, Integer grade, Boolean validation, Instant assessedAt, Staff assessedBy) {
    return new DeclaredActivityAssessment(
        comment, grade, validation, requestedAt, assessedAt, assessedBy, modifiedAt, consultedAt);
  }

  public DeclaredActivityAssessment modify(
      String comment, Integer grade, Boolean validation, Instant modifiedAt, Staff assessedBy) {
    return new DeclaredActivityAssessment(
        comment, grade, validation, requestedAt, assessedAt, assessedBy, modifiedAt, consultedAt);
  }

  public DeclaredActivityAssessment consult(Instant consultedAt) {
    return new DeclaredActivityAssessment(
        comment, grade, validation, requestedAt, assessedAt, assessedBy, modifiedAt, consultedAt);
  }
}
