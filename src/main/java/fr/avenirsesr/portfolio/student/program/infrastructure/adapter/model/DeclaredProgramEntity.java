package fr.avenirsesr.portfolio.student.program.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.student.program.domain.model.enums.EProgramStatus;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "declared_program",
    indexes = {
      @Index(name = "idx_declared_program_student", columnList = "student_id"),
      @Index(name = "idx_declared_program_student_status", columnList = "student_id, status")
    })
@NoArgsConstructor
@Getter
@Setter
@AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = true))
public class DeclaredProgramEntity extends PeriodEntity<LocalDate> {

  @ManyToOne(optional = false)
  private StudentEntity student;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EProgramStatus status;

  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @Column(length = DESCRIPTION_LENGTH)
  private String description;

  @Column(nullable = false, length = ORGANIZATION_LENGTH)
  private String organization;

  @Column(length = RESULT_LENGTH)
  private String result;

  @Column(name = "source_of_information", length = SOURCE_OF_INFORMATION_LENGTH)
  private String sourceOfInformation;

  @Column(nullable = false)
  private boolean valorized;

  private DeclaredProgramEntity(
      UUID id,
      StudentEntity student,
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
    setId(id);
    this.student = student;
    setStartDate(startDate);
    setEndDate(endDate);
    this.status = status;
    this.title = title;
    this.description = description;
    this.organization = organization;
    this.result = result;
    this.sourceOfInformation = sourceOfInformation;
    this.valorized = valorized;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static DeclaredProgramEntity of(
      UUID id,
      StudentEntity student,
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
    return new DeclaredProgramEntity(
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
