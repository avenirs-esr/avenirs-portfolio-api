package fr.avenirsesr.portfolio.activity.domain.model;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
  private File banner;
  @Getter private List<String> links;
  @Getter private List<File> files;
  @Getter private int traceAllowedAssociations;
  @Getter private int feedbackAllowedIterations;
  @Getter private boolean enableReflection;

  private static final int DEFAULT_TRACE_ALLOWED_ASSOCIATION = -1;
  private static final int DEFAULT_FEEDBACK_ALLOWED_ITERATIONS = -1;

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
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      boolean enableReflection,
      File banner,
      List<String> links,
      List<File> files) {
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
    this.banner = banner;
    this.links = new ArrayList<>(links == null ? List.of() : links);
    this.files = new ArrayList<>(files == null ? List.of() : files);
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
        true,
        null,
        List.of(),
        List.of());
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
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      boolean enableReflection,
      File banner,
      List<String> links,
      List<File> files) {
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
        enableReflection,
        banner,
        links,
        files);
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

  public Optional<File> getBanner() {
    return Optional.ofNullable(banner);
  }

  public void addLinks(List<String> links) {
    this.links.addAll(links);
  }

  public void addFile(File file) {
    this.files.add(file);
  }

  public void removeFile(UUID fileId) {
    this.files.removeIf(file -> file.getId().equals(fileId));
  }
}
