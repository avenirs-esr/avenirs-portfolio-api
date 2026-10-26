package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EAssessmentStatus;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;

public record DeclaredActivityAssessment(
    String comment,
    Integer grade,
    Boolean validation,
    EAssessmentStatus status,
    Instant requestedAt,
    Instant assessedAt,
    Staff assessedBy) {

  public static DeclaredActivityAssessment requested(Instant requestedAt) {
    return new DeclaredActivityAssessment(
        null, null, null, EAssessmentStatus.DRAFT, requestedAt, null, null);
  }

  public static DeclaredActivityAssessment draft(
      String comment, Integer grade, Boolean validation) {
    return new DeclaredActivityAssessment(
        comment, grade, validation, EAssessmentStatus.DRAFT, null, null, null);
  }

  public boolean isPublished() {
    return status == EAssessmentStatus.PUBLISHED;
  }

  public DeclaredActivityAssessment withDraft(DeclaredActivityAssessment draft) {
    return new DeclaredActivityAssessment(
        draft.comment(),
        draft.grade(),
        draft.validation(),
        EAssessmentStatus.DRAFT,
        requestedAt,
        assessedAt,
        assessedBy);
  }
}
