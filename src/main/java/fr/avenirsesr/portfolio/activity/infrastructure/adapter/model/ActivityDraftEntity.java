package fr.avenirsesr.portfolio.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity_draft",
    indexes = {@Index(name = "idx_activity_draft_thematic", columnList = "thematic")})
@AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = true))
@AttributeOverride(name = "startDate", column = @Column(name = "start_date", nullable = true))
@NoArgsConstructor
@Getter
@Setter
public class ActivityDraftEntity extends PeriodEntity<LocalDate> {
  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private StaffEntity author;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EActivityThematic thematic;

  @Column(length = SUMMARY_LENGTH)
  private String summary;

  @Column(length = RICH_DESCRIPTION_LENGTH)
  private String description;

  @Column(name = "execution_period_info", length = ACTIVITY_EXECUTION_PERIOD_INFO)
  private String executionPeriodInfo;

  @Column(name = "execution_period_info_summary", length = TITLE_LENGTH)
  private String executionPeriodInfoSummary;

  @Column(name = "trace_allowed_associations", nullable = false)
  private int traceAllowedAssociations;

  @Column(name = "feedback_allowed_iterations", nullable = false)
  private int feedbackAllowedIterations;

  @Column(name = "enable_reflection")
  private boolean enableReflection;

  @OneToOne
  @JoinColumn(name = "banner_id")
  private FileEntity banner;

  @ElementCollection
  @CollectionTable(
      name = "activity_draft_links",
      joinColumns = @JoinColumn(name = "activity_draft_id"))
  @Column(name = "link", nullable = false)
  private List<@Size(max = LINK_LENGTH, message = "link can not exceed {max} characters") String>
      links = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "activity_draft_files",
      joinColumns = @JoinColumn(name = "activity_draft_id"),
      inverseJoinColumns = @JoinColumn(name = "file_id"))
  @OrderBy("createdAt")
  private List<FileEntity> files = new ArrayList<>();

  public ActivityDraftEntity(
      UUID id,
      String title,
      StaffEntity author,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      LocalDate startDate,
      LocalDate endDate,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      boolean enableReflection,
      FileEntity banner,
      List<String> links,
      List<FileEntity> files,
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
    this.startDate = startDate;
    this.endDate = endDate;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.enableReflection = enableReflection;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
    this.banner = banner;
    this.links = links;
    this.files = files;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ActivityDraftEntity of(
      UUID id,
      String title,
      StaffEntity author,
      EActivityThematic thematic,
      String summary,
      String description,
      String executionPeriodInfo,
      String executionPeriodInfoSummary,
      LocalDate startDate,
      LocalDate endDate,
      int traceAllowedAssociations,
      int feedbackAllowedIterations,
      boolean enableReflection,
      FileEntity banner,
      List<String> links,
      List<FileEntity> files,
      Instant createdAt,
      Instant updatedAt) {
    return new ActivityDraftEntity(
        id,
        title,
        author,
        thematic,
        summary,
        description,
        executionPeriodInfo,
        executionPeriodInfoSummary,
        startDate,
        endDate,
        traceAllowedAssociations,
        feedbackAllowedIterations,
        enableReflection,
        banner,
        links,
        files,
        createdAt,
        updatedAt);
  }
}
