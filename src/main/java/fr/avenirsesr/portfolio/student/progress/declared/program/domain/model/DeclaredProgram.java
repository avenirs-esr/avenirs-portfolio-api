package fr.avenirsesr.portfolio.student.progress.declared.program.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;

@Getter
public class DeclaredProgram extends AvenirsBaseModel {
  private final Student student;
  private EProgramStatus status;
  private String title;
  private String description;
  private String organization;
  private String result;
  private String sourceOfInformation;
  private String link;
  private LocalDate startDate;
  private LocalDate endDate;

  private DeclaredProgram(
      UUID id,
      Student student,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.student = student;
    this.title = title;
    this.description = description;
    this.organization = organization;
    this.status = status;
    this.startDate = startDate;
    this.endDate = endDate;
    this.result = result;
    this.sourceOfInformation = sourceOfInformation;
    this.link = link;
  }

  public static DeclaredProgram create(
      Student student,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate) {
    return new DeclaredProgram(
        UUID.randomUUID(),
        student,
        status,
        title,
        description,
        organization,
        result,
        sourceOfInformation,
        link,
        startDate,
        endDate,
        Instant.now(),
        Instant.now());
  }

  public static DeclaredProgram of(
      UUID id,
      Student student,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      String link,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredProgram(
        id,
        student,
        status,
        title,
        description,
        organization,
        result,
        sourceOfInformation,
        link,
        startDate,
        endDate,
        createdAt,
        updatedAt);
  }
}
