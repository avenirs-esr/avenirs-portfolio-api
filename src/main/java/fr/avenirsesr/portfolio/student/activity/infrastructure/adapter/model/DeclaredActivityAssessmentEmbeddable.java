package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeclaredActivityAssessmentEmbeddable {
  @Size(max = RICH_TEXT_LENGTH, message = "assessment comment can not exceed {max} characters")
  @Column(name = "assessment_comment", columnDefinition = "TEXT")
  private String comment;

  @Column(name = "assessment_grade")
  private Integer grade;

  @Column(name = "assessment_validation")
  private Boolean validation;
}
