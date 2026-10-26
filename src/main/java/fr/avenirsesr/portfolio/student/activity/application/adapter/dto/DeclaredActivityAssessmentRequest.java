package fr.avenirsesr.portfolio.student.activity.application.adapter.dto;

import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivityAssessment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema()
public record DeclaredActivityAssessmentRequest(
    @Schema(nullable = true) String comment,
    @Schema(nullable = true) Integer grade,
    @Schema(nullable = true) Boolean validation) {
  public DeclaredActivityAssessment toDraft() {
    return DeclaredActivityAssessment.draft(comment, grade, validation);
  }
}
