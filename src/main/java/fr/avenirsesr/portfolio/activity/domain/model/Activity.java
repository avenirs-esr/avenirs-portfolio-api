package fr.avenirsesr.portfolio.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Activity extends AvenirsBaseModel {
  private final Staff author;
  private String title;
  private EActivityThematic thematic;
  private String summary;
  private String description;
  private String executionPeriodInfo;
  private boolean enableReflection;
  private int traceAllowedAssociations;
  private int feedbackAllowedIterations;

  @Getter(AccessLevel.NONE)
  private String executionPeriodInfoSummary;

  private Activity(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.author = author;
    this.title = title;
    this.thematic = thematic;
    this.summary = summary;
    this.description = description;
    this.executionPeriodInfo = executionPeriodInfo;
    this.executionPeriodInfoSummary = executionPeriodInfoSummary;
    this.enableReflection = enableReflection;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
  }

  public static Activity create(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations) {
    Instant now = Instant.now();
    return new Activity(
        id,
        author,
        title,
        thematic,
        summary,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        enableReflection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        now,
        now);
  }

  public static Activity toDomain(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      Instant createdAt,
      Instant updatedAt) {
    return new Activity(
        id,
        author,
        title,
        thematic,
        summary,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        enableReflection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        createdAt,
        updatedAt);
  }

  public Optional<String> getExecutionPeriodInfoSummary() {
    return Optional.ofNullable(executionPeriodInfoSummary);
  }
}
