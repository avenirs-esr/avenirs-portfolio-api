package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.RICH_TEXT_LENGTH;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Entity
@Table(
    name = "declared_activity",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "activity_id"})},
    indexes = {@Index(name = "idx_ap_student", columnList = "student_id")})
@AttributeOverride(name = "endDate", column = @Column(name = "end_date", nullable = true))
@AttributeOverride(name = "startDate", column = @Column(name = "start_date", nullable = true))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeclaredActivityEntity extends PeriodEntity<LocalDate> {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private StudentEntity student;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "activity_id")
  private ActivityEntity activity;

  @Column(name = "started_at")
  private Instant startedAt;

  @Size(max = RICH_TEXT_LENGTH, message = "reflection can not exceed {max} characters")
  @Column(columnDefinition = "TEXT")
  private String reflection;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Formula("CASE WHEN finished_at IS NULL THEN 0 ELSE 1 END")
  private Integer isFinishedOrder;

  @Column(nullable = false)
  private boolean valorized;

  private DeclaredActivityEntity(
      UUID id,
      StudentEntity student,
      ActivityEntity activity,
      Instant startedAt,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
    this.student = student;
    this.activity = activity;
    this.startedAt = startedAt;
    this.reflection = reflection;
    this.startDate = startDate;
    this.endDate = endDate;
    this.finishedAt = finishedAt;
    this.valorized = valorized;
  }

  public static DeclaredActivityEntity of(
      UUID id,
      StudentEntity student,
      ActivityEntity activity,
      Instant startedAt,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt,
      boolean valorized,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredActivityEntity(
        id,
        student,
        activity,
        startedAt,
        reflection,
        startDate,
        endDate,
        finishedAt,
        valorized,
        createdAt,
        updatedAt);
  }

  @Override
  public String toString() {
    return "DeclaredActivityEntity[%s]".formatted(getId());
  }
}
