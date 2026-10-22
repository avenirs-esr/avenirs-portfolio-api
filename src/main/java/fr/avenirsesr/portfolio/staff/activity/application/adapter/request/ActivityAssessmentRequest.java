package fr.avenirsesr.portfolio.staff.activity.application.adapter.request;

import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityAssessment;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema()
public record ActivityAssessmentRequest(
    @Schema(nullable = true) Boolean comment,
    @Schema(nullable = true) Integer grade,
    @Schema(nullable = true) Boolean validation) {
  public ActivityAssessment toDomain() {
    return new ActivityAssessment(comment, grade, validation);
  }
}
