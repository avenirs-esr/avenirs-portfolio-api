package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EAssessmentStatus;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import java.time.Instant;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "assessment_status")
  private EAssessmentStatus status;

  @Column(name = "assessment_requested_at")
  private Instant requestedAt;

  @Column(name = "assessment_assessed_at")
  private Instant assessedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_assessed_by_id")
  private StaffEntity assessedBy;
}
