package fr.avenirsesr.portfolio.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
public class ActivityDraft extends AvenirsBaseModel {
  @Getter private final Staff author;
  @Getter private String title;
  @Getter private EActivityThematic thematic;

  private String summary;
  private String description;
  private String executionPeriodInfo;
  private String executionPeriodInfoSummary;
  private Integer traceAllowedAssociations;
  private Integer feedbackAllowedIterations;
  @Getter private boolean enableReflection;

  private static final int DEFAULT_TRACE_ALLOWED_ASSOCIATION = 10;
  private static final int DEFAULT_FEEDBACK_ALLOWED_ITERATIONS = 10;

  private ActivityDraft(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      String title,
      Staff author,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      Integer traceAllowedAssociations,
      Integer feedbackAllowedIterations,
      boolean enableReflection) {
    super(id, createdAt, updatedAt);
    this.title = title;
    this.author = author;
    this.thematic = thematic;
    this.summary = summary;
    this.description = description;
    this.executionPeriodInfo = executionPeriodInfo;
    this.executionPeriodInfoSummary = executionPeriodInfoSummary;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.enableReflection = enableReflection;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
  }

  public static ActivityDraft create(String title, Staff createdBy) {
    return new ActivityDraft(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        title,
        createdBy,
        EActivityThematic.TRANSVERSAL,
        null,
        null,
        null,
        null,
        DEFAULT_TRACE_ALLOWED_ASSOCIATION,
        DEFAULT_FEEDBACK_ALLOWED_ITERATIONS,
        true);
  }

  public static ActivityDraft toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      String title,
      Staff createdBy,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      Integer traceAllowedAssociations,
      Integer feedbackAllowedIterations,
      boolean enableReflection) {
    return new ActivityDraft(
        id,
        createdAt,
        updatedAt,
        title,
        createdBy,
        thematic,
        summary,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        enableReflection);
  }

  public Optional<String> getSummary() {
    return Optional.ofNullable(summary);
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public Optional<String> getExecutionPeriodInfo() {
    return Optional.ofNullable(executionPeriodInfo);
  }

  public Optional<String> getExecutionPeriodInfoSummary() {
    return Optional.ofNullable(executionPeriodInfoSummary);
  }

  public Optional<Integer> getTraceAllowedAssociations() {
    return Optional.ofNullable(traceAllowedAssociations);
  }

  public Optional<Integer> getFeedbackAllowedIterations() {
    return Optional.ofNullable(feedbackAllowedIterations);
  }
}
