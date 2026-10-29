package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EActivityAssessmentStatus;
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
    Instant consultedAt,
    Instant openedAt) {

  public static DeclaredActivityAssessment requested(Instant requestedAt) {
    return new DeclaredActivityAssessment(
        null, null, null, requestedAt, null, null, null, null, null);
  }

  public boolean isSent() {
    return assessedAt != null;
  }

  public boolean isOpened() {
    return openedAt != null;
  }

  public EActivityAssessmentStatus status() {
    if (isSent()) {
      return EActivityAssessmentStatus.SENT;
    }
    return isOpened() ? EActivityAssessmentStatus.OPENED : EActivityAssessmentStatus.NEW;
  }

  public DeclaredActivityAssessment send(
      String comment, Integer grade, Boolean validation, Instant assessedAt, Staff assessedBy) {
    return new DeclaredActivityAssessment(
        comment,
        grade,
        validation,
        requestedAt,
        assessedAt,
        assessedBy,
        modifiedAt,
        consultedAt,
        openedAt);
  }

  public DeclaredActivityAssessment modify(
      String comment, Integer grade, Boolean validation, Instant modifiedAt, Staff assessedBy) {
    return new DeclaredActivityAssessment(
        comment,
        grade,
        validation,
        requestedAt,
        assessedAt,
        assessedBy,
        modifiedAt,
        consultedAt,
        openedAt);
  }

  public DeclaredActivityAssessment consult(Instant consultedAt) {
    return new DeclaredActivityAssessment(
        comment,
        grade,
        validation,
        requestedAt,
        assessedAt,
        assessedBy,
        modifiedAt,
        consultedAt,
        openedAt);
  }

  public DeclaredActivityAssessment open(Instant openedAt) {
    return new DeclaredActivityAssessment(
        comment,
        grade,
        validation,
        requestedAt,
        assessedAt,
        assessedBy,
        modifiedAt,
        consultedAt,
        openedAt);
  }
}
