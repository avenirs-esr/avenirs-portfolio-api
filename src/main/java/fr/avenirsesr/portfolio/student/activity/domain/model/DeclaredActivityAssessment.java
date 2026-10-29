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
    Instant studentConsultedAt,
    Instant staffOpenedAt) {

  public static DeclaredActivityAssessment requested(Instant requestedAt) {
    return new DeclaredActivityAssessment(
        null, null, null, requestedAt, null, null, null, null, null);
  }

  public boolean isSent() {
    return assessedAt != null;
  }

  public boolean isOpenedByStaff() {
    return staffOpenedAt != null;
  }

  public EActivityAssessmentStatus status() {
    if (isSent()) {
      return EActivityAssessmentStatus.SENT;
    }
    return isOpenedByStaff() ? EActivityAssessmentStatus.OPENED : EActivityAssessmentStatus.NEW;
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
        studentConsultedAt,
        staffOpenedAt);
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
        studentConsultedAt,
        staffOpenedAt);
  }

  public DeclaredActivityAssessment consultByStudent(Instant studentConsultedAt) {
    return new DeclaredActivityAssessment(
        comment,
        grade,
        validation,
        requestedAt,
        assessedAt,
        assessedBy,
        modifiedAt,
        studentConsultedAt,
        staffOpenedAt);
  }

  public DeclaredActivityAssessment openByStaff(Instant staffOpenedAt) {
    return new DeclaredActivityAssessment(
        comment,
        grade,
        validation,
        requestedAt,
        assessedAt,
        assessedBy,
        modifiedAt,
        studentConsultedAt,
        staffOpenedAt);
  }
}
