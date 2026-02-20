package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.REFLECTION_LENGTH;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

  @Column(name = "has_started", nullable = false)
  private boolean hasStarted;

  @Size(max = REFLECTION_LENGTH, message = "reflection can not exceed {max} characters")
  private String reflection;

  @Column(name = "finished_at")
  private Instant finishedAt;

  @Formula("CASE WHEN finished_at IS NULL THEN 0 ELSE 1 END")
  private Integer isFinishedOrder;

  private DeclaredActivityEntity(
      UUID id,
      StudentEntity student,
      ActivityEntity activity,
      boolean hasStarted,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt) {
    setId(id);
    this.student = student;
    this.activity = activity;
    this.hasStarted = hasStarted;
    this.reflection = reflection;
    this.startDate = startDate;
    this.endDate = endDate;
    this.finishedAt = finishedAt;
  }

  public static DeclaredActivityEntity of(
      UUID id,
      StudentEntity student,
      ActivityEntity activity,
      boolean hasStarted,
      String reflection,
      LocalDate startDate,
      LocalDate endDate,
      Instant finishedAt) {
    return new DeclaredActivityEntity(
        id, student, activity, hasStarted, reflection, startDate, endDate, finishedAt);
  }

  @Override
  public String toString() {
    return "DeclaredActivityEntity[%s]".formatted(getId());
  }
}
