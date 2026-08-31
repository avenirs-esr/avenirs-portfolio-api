package fr.avenirsesr.portfolio.student.experience.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclaredExperience extends AvenirsBaseModel {
  private final Student student;
  private String title;
  private EExperienceType experienceType;
  private String organization;
  private String activitySector;
  private String location;
  private String description;
  private String sourceOfInformation;
  private String summary;
  private String externalLink;
  private String result;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean valorized;

  private DeclaredExperience(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      Student student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate,
      boolean valorized) {
    super(id, createdAt, updatedAt);
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
    this.result = result;
    this.startDate = startDate;
    this.endDate = endDate;
    this.valorized = valorized;
  }

  public static DeclaredExperience create(
      Student student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate) {
    return new DeclaredExperience(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
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
        result,
        startDate,
        endDate,
        false);
  }

  public static DeclaredExperience toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      Student student,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate,
      boolean valorized) {
    return new DeclaredExperience(
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
        result,
        startDate,
        endDate,
        valorized);
  }
}
