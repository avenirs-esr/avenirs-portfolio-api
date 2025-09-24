package fr.avenirsesr.portfolio.ams.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.common.temporal.infrastructure.adapter.model.PeriodEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
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

@Entity
@Table(name = "ams")
@NoArgsConstructor
@Getter
@Setter
public class AMSEntity extends PeriodEntity<Instant> {

  @ManyToOne(optional = false)
  private UserEntity user;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EAmsStatus status;

  @OneToMany(
      mappedBy = "ams",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<AMSTranslationEntity> translations = new HashSet<>();

  private AMSEntity(
      UUID id, UserEntity user, EAmsStatus status, Instant startDate, Instant endDate) {
    setId(id);
    this.user = user;
    setStartDate(startDate);
    setEndDate(endDate);
    this.status = status;
  }

  public static AMSEntity of(
      UUID id, UserEntity user, EAmsStatus status, Instant startDate, Instant endDate) {
    return new AMSEntity(id, user, status, startDate, endDate);
  }
}
