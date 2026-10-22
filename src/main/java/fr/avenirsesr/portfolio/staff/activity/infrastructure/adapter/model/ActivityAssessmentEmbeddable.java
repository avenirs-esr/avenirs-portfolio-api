package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ActivityAssessmentEmbeddable {
  @Column(name = "assessment_comment")
  private Boolean comment;

  @Column(name = "assessment_grade")
  @Min(1)
  @Max(20)
  private Integer grade;

  @Column(name = "assessment_validation")
  private Boolean validation;
}
