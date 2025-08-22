package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EDurationUnit;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Program extends AvenirsBaseModel {
  private final Institution institution;
  private final String name;

  @Getter(AccessLevel.NONE)
  private final EDurationUnit durationUnit;

  @Getter(AccessLevel.NONE)
  private final Integer durationCount;

  private boolean isAPC;

  private Program(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      EDurationUnit durationUnit,
      Integer durationCount,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.institution = institution;
    this.name = name;
    this.isAPC = isAPC;
    this.durationUnit = durationUnit;
    this.durationCount = durationCount;
  }

  public static Program create(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      EDurationUnit durationUnit,
      Integer durationCount) {
    return new Program(
        id, institution, name, isAPC, durationUnit, durationCount, Instant.now(), Instant.now());
  }

  public static Program toDomain(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      EDurationUnit durationUnit,
      Integer durationCount,
      Instant createdAt,
      Instant updatedAt) {
    return new Program(
        id, institution, name, isAPC, durationUnit, durationCount, createdAt, updatedAt);
  }

  public Optional<EDurationUnit> getDurationUnit() {
    return Optional.ofNullable(durationUnit);
  }

  public Optional<Integer> getDurationCount() {
    return Optional.ofNullable(durationCount);
  }
}
