package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.temporal.Temporal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class PeriodEntity<T extends Temporal> {
  @Column(name = "start_date", nullable = false)
  protected T startDate;

  @Column(name = "end_date", nullable = false)
  protected T endDate;
}
