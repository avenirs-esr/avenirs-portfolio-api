package fr.avenirsesr.portfolio.student.program.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.student.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclaredProgram extends AvenirsBaseModel {
  private final Student student;
  private EProgramStatus status;
  private String title;
  private String description;
  private String organization;
  private String result;
  private String sourceOfInformation;
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean valorized;

  private DeclaredProgram(
      UUID id,
      Student student,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
      LocalDate startDate,
      LocalDate endDate,
      boolean valorized,
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
    this.valorized = valorized;
  }

  public static DeclaredProgram create(
      Student student,
      EProgramStatus status,
      String title,
      String description,
      String organization,
      String result,
      String sourceOfInformation,
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
        startDate,
        endDate,
        false,
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
      LocalDate startDate,
      LocalDate endDate,
      boolean valorized,
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
        startDate,
        endDate,
        valorized,
        createdAt,
        updatedAt);
  }
}
