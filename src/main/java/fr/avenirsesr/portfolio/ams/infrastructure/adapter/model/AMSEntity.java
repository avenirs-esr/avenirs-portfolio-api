package fr.avenirsesr.portfolio.ams.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StudentEntity;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(
    name = "ams",
    indexes = {
      @Index(name = "idx_ams_student", columnList = "student_id"),
      @Index(name = "idx_ams_student_status", columnList = "student_id, status")
    })
@NoArgsConstructor
@Getter
@Setter
public class AMSEntity extends PeriodEntity<Instant> {

  @ManyToOne(optional = false)
  private StudentEntity student;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EAmsStatus status;

  @OneToMany(
      mappedBy = "ams",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Filter(name = "langFilter")
  private Set<AMSTranslationEntity> translations = new HashSet<>();

  private AMSEntity(
      UUID id, StudentEntity student, EAmsStatus status, Instant startDate, Instant endDate) {
    setId(id);
    this.student = student;
    setStartDate(startDate);
    setEndDate(endDate);
    this.status = status;
  }

  public static AMSEntity of(
      UUID id, StudentEntity student, EAmsStatus status, Instant startDate, Instant endDate) {
    return new AMSEntity(id, student, status, startDate, endDate);
  }
}
