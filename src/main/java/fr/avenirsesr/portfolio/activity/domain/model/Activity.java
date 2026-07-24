package fr.avenirsesr.portfolio.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
  private String description;

  @Getter(AccessLevel.NONE)
  private String executionPeriodInfo;

  @Getter(AccessLevel.NONE)
  private String executionPeriodInfoSummary;

  @Getter(AccessLevel.NONE)
  private LocalDate startDate;

  @Getter(AccessLevel.NONE)
  private LocalDate endDate;

  @Getter(AccessLevel.NONE)
  private File banner;

  private List<String> links;
  private List<File> files;

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
      LocalDate startDate,
      LocalDate endDate,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      File banner,
      List<String> links,
      List<File> files,
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
    this.startDate = startDate;
    this.endDate = endDate;
    this.enableReflection = enableReflection;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
    this.banner = banner;
    this.links = new ArrayList<>(links == null ? List.of() : links);
    this.files = new ArrayList<>(files == null ? List.of() : files);
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
      LocalDate startDate,
      LocalDate endDate,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      File banner,
      List<String> links,
      List<File> files) {
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
        startDate,
        endDate,
        enableReflection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        banner,
        links,
        files,
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
      LocalDate startDate,
      LocalDate endDate,
      boolean enableReflection,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      File banner,
      List<String> links,
      List<File> files,
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
        startDate,
        endDate,
        enableReflection,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        banner,
        links,
        files,
        createdAt,
        updatedAt);
  }

  public Optional<String> getExecutionPeriodInfoSummary() {
    return Optional.ofNullable(executionPeriodInfoSummary);
  }

  public Optional<String> getExecutionPeriodInfo() {
    return Optional.ofNullable(executionPeriodInfo);
  }

  public Optional<LocalDate> getStartDate() {
    return Optional.ofNullable(startDate);
  }

  public Optional<LocalDate> getEndDate() {
    return Optional.ofNullable(endDate);
  }

  public Optional<File> getBanner() {
    return Optional.ofNullable(banner);
  }

  public void addFile(File file) {
    this.files.add(file);
  }

  public void removeFile(UUID fileId) {
    this.files.removeIf(file -> file.getId().equals(fileId));
  }
}
