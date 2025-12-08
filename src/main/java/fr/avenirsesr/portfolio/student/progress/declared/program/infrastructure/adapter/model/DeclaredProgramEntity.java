package fr.avenirsesr.portfolio.student.progress.declared.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.enums.EProgramStatus;
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

  @Column(nullable = false)
  private String title;

  @Column() private String description;

  @Column(nullable = false)
  private String organization;

  @Column() private String result;

  @Column(name = "source_of_information")
  private String sourceOfInformation;

  @Column() private String link;

  private DeclaredProgramEntity(
      UUID id,
      StudentEntity student,
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
    this.link = link;
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
      String link,
      LocalDate startDate,
      LocalDate endDate,
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
        link,
        startDate,
        endDate,
        createdAt,
        updatedAt);
  }
}
