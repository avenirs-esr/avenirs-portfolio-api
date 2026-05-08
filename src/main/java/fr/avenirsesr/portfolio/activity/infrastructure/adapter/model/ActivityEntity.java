package fr.avenirsesr.portfolio.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity",
    indexes = {@Index(name = "idx_activity_thematic", columnList = "thematic")})
@NoArgsConstructor
@Getter
@Setter
public class ActivityEntity extends AvenirsBaseEntity {

  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private StaffEntity author;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EActivityThematic thematic;

  @Column(nullable = false, length = SUMMARY_LENGTH)
  private String summary;

  @Column(nullable = false, length = RICH_TEXT_LENGTH)
  private String description;

  @Column(name = "execution_period_info", nullable = false, length = ACTIVITY_EXECUTION_PERIOD_INFO)
  private String executionPeriodInfo;

  @Column(name = "execution_period_info_summary", length = TITLE_LENGTH)
  private String executionPeriodInfoSummary;

  @Column(name = "trace_allowed_associations", nullable = false)
  private int traceAllowedAssociations;

  @Column(name = "feedback_allowed_iterations", nullable = false)
  private int feedbackAllowedIterations;

  @Column(name = "enable_reflection", nullable = false)
  private boolean enableReflection;

  private ActivityEntity(
      UUID id,
      StaffEntity author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      boolean enableReflection,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.title = title;
    this.author = author;
    this.thematic = thematic;
    this.summary = summary;
    this.description = description;
    this.executionPeriodInfo = executionPeriodInfo;
    this.executionPeriodInfoSummary = executionPeriodInfoSummary;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
    this.enableReflection = enableReflection;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ActivityEntity of(
      UUID id,
      StaffEntity author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      boolean enableReflection,
      Instant createdAt,
      Instant updatedAt) {

    return new ActivityEntity(
        id,
        author,
        title,
        thematic,
        summary,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        enableReflection,
        createdAt,
        updatedAt);
  }
}
