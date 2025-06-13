package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class PeriodEntity {
  @Column(name = "start_date", nullable = false)
  protected Instant startDate;

  @Column(name = "end_date", nullable = false)
  protected Instant endDate;
}
