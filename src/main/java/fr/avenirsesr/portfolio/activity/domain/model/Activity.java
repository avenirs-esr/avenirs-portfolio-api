package fr.avenirsesr.portfolio.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.file.domain.model.File;
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
  private EActivityStatus status;
  private boolean enableReflection;
  private int traceAllowedAssociations;
  private int feedbackAllowedIterations;

  @Getter(AccessLevel.NONE)
  private String description;

  @Getter(AccessLevel.NONE)
  private String executionPeriodInfo;

  @Getter(AccessLevel.NONE)
  private String executionPeriodInfoSummary;

  @Getter(AccessLevel.NONE)
  private File banner;

  private Activity(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      EActivityStatus status,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      File banner,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.author = author;
    this.title = title;
    this.thematic = thematic;
    this.summary = summary;
    this.status = status;
    this.description = description;
    this.executionPeriodInfo = executionPeriodInfo;
    this.executionPeriodInfoSummary = executionPeriodInfoSummary;
    this.enableReflection = enableReflection;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
    this.banner = banner;
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
      int feedbackAllowedIterations,
      File banner) {
    Instant now = Instant.now();
    return new Activity(
        id,
        author,
        title,
        thematic,
        summary,
        EActivityStatus.PUBLISHED,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        enableReflection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        banner,
        now,
        now);
  }

  public static Activity toDomain(
      UUID id,
      Staff author,
      String title,
      EActivityThematic thematic,
      String summary,
      EActivityStatus status,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      File banner,
      Instant createdAt,
      Instant updatedAt) {
    return new Activity(
        id,
        author,
        title,
        thematic,
        summary,
        status,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        enableReflection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        banner,
        createdAt,
        updatedAt);
  }

  public Optional<String> getExecutionPeriodInfoSummary() {
    return Optional.ofNullable(executionPeriodInfoSummary);
  }

  public Optional<String> getExecutionPeriodInfo() {
    return Optional.ofNullable(executionPeriodInfo);
  }

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public Optional<File> getBanner() {
    return Optional.ofNullable(banner);
  }
}
