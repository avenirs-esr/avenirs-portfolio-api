package fr.avenirsesr.portfolio.student.progress.declared.experience.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "declared_experience",
    indexes = {
      @Index(name = "idx_declared_experience_student", columnList = "student_id"),
      @Index(name = "idx_declared_experience_type", columnList = "experience_type"),
      @Index(name = "idx_declared_experience_start_date", columnList = "start_date"),
      @Index(name = "idx_declared_experience_end_date", columnList = "end_date")
    })
@AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = true))
@NoArgsConstructor
@Getter
@Setter
public class DeclaredExperienceEntity extends PeriodEntity<LocalDate> {
  @ManyToOne(optional = false)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @Column(nullable = false)
  @Size(max = TITLE_LENGTH, message = "title can not exceed {max} characters")
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(name = "experience_type")
  private EExperienceType experienceType;

  @Column(nullable = false)
  @Size(max = ORGANIZATION_LENGTH, message = "organization can not exceed {max} characters")
  private String organization;

  @Size(max = ACTIVITY_SECTOR_LENGTH, message = "activity sector can not exceed {max} characters")
  @Column(name = "activity_sector")
  private String activitySector;

  @Size(max = LOCATION_LENGTH, message = "location can not exceed {max} characters")
  private String location;

  @Size(max = DESCRIPTION_LENGTH, message = "description can not exceed {max} characters")
  @Column(length = DESCRIPTION_LENGTH)
  private String description;

  @Size(
      max = SOURCE_OF_INFORMATION_LENGTH,
      message = "source of information can not exceed {max} characters")
  @Column(name = "source_of_information")
  private String sourceOfInformation;

  @Size(max = SUMMARY_LENGTH, message = "summary can not exceed {max} characters")
  @Column(length = SUMMARY_LENGTH)
  private String summary;

  @Column(name = "external_link")
  private String externalLink;

  private DeclaredExperienceEntity(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      StudentEntity student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    this.setId(id);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.student = student;
    this.title = title;
    this.experienceType = experienceType;
    this.organization = organization;
    this.activitySector = activitySector;
    this.location = location;
    this.description = description;
    this.sourceOfInformation = sourceOfInformation;
    this.summary = summary;
    this.externalLink = externalLink;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static DeclaredExperienceEntity of(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      StudentEntity student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      LocalDate startDate,
      LocalDate endDate) {
    return new DeclaredExperienceEntity(
        id,
        createdAt,
        updatedAt,
        student,
        title,
        experienceType,
        organization,
        activitySector,
        location,
        description,
        sourceOfInformation,
        summary,
        externalLink,
        startDate,
        endDate);
  }
}
