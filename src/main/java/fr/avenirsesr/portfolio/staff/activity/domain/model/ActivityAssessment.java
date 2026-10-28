package fr.avenirsesr.portfolio.staff.activity.domain.model;

import fr.avenirsesr.portfolio.staff.activity.domain.exception.ActivityAssessmentException;

public record ActivityAssessment(Boolean comment, Integer grade, Boolean validation) {
  public static final int MIN_GRADE = 1;
  public static final int MAX_GRADE = 20;

  public boolean isCommentEnabled() {
    return Boolean.TRUE.equals(comment);
  }

  public boolean isGradeEnabled() {
    return grade != null;
  }

  public boolean isValidationEnabled() {
    return Boolean.TRUE.equals(validation);
  }

  public boolean isCommentRequired() {
    return isCommentEnabled() && !isGradeEnabled() && !isValidationEnabled();
  }

  public boolean isEmpty() {
    return !isCommentEnabled() && !isGradeEnabled() && !isValidationEnabled();
  }

  public void validate() {
    if (isGradeEnabled() && isValidationEnabled()) {
      throw new ActivityAssessmentException(
          "An assessment cannot enable both a grade and a validation");
    }
    if (isGradeEnabled() && (grade < MIN_GRADE || grade > MAX_GRADE)) {
      throw new ActivityAssessmentException(
          "The assessment grade scale must be between %d and %d".formatted(MIN_GRADE, MAX_GRADE));
    }
  }
}
