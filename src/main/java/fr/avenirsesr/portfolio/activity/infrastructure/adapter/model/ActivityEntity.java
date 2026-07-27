package fr.avenirsesr.portfolio.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityStatus;
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
    name = "activity",
    indexes = {@Index(name = "idx_activity_thematic", columnList = "thematic")})
@AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = true))
@AttributeOverride(name = "startDate", column = @Column(name = "start_date", nullable = true))
@NoArgsConstructor
@Getter
@Setter
public class ActivityEntity extends PeriodEntity<LocalDate> {

  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private StaffEntity author;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EActivityThematic thematic;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EActivityStatus status;

  @Column(nullable = false, length = SUMMARY_LENGTH)
  private String summary;

  @Column(nullable = false, length = RICH_DESCRIPTION_LENGTH)
  private String description;

  @Column(
      name = "recommended_completion_contexts",
      length = ACTIVITY_RECOMMENDED_COMPLETION_CONTEXTS)
  private String recommendedCompletionContexts;

  @Column(name = "trace_allowed_associations", nullable = false)
  private int traceAllowedAssociations;

  @Column(name = "feedback_allowed_iterations", nullable = false)
  private int feedbackAllowedIterations;

  @Column(name = "enable_reflection", nullable = false)
  private boolean enableReflection;

  @OneToOne
  @JoinColumn(name = "banner_id")
  private FileEntity banner;

  @ElementCollection
  @CollectionTable(name = "activity_links", joinColumns = @JoinColumn(name = "activity_id"))
  @Column(name = "link", nullable = false)
  private List<@Size(max = LINK_LENGTH, message = "link can not exceed {max} characters") String>
      links = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "activity_files",
      joinColumns = @JoinColumn(name = "activity_id"),
      inverseJoinColumns = @JoinColumn(name = "file_id"))
  @OrderBy("createdAt")
  private List<FileEntity> files = new ArrayList<>();

  private ActivityEntity(
      UUID id,
      StaffEntity author,
      String title,
      EActivityThematic thematic,
      String summary,
      EActivityStatus status,
      String description,
      String recommendedCompletionContexts,
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
    this.status = status;
    this.description = description;
    this.recommendedCompletionContexts = recommendedCompletionContexts;
    this.startDate = startDate;
    this.endDate = endDate;
    this.traceAllowedAssociations = traceAllowedAssociations;
    this.feedbackAllowedIterations = feedbackAllowedIterations;
    this.enableReflection = enableReflection;
    this.banner = banner;
    this.links = links;
    this.files = files;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static ActivityEntity of(
      UUID id,
      StaffEntity author,
      String title,
      EActivityThematic thematic,
      String summary,
      EActivityStatus status,
      String description,
      String recommendedCompletionContexts,
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

    return new ActivityEntity(
        id,
        author,
        title,
        thematic,
        summary,
        status,
        description,
        recommendedCompletionContexts,
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
