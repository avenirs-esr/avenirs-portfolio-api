package fr.avenirsesr.portfolio.programprogress.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EDurationUnit;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Program {
  private final UUID id;
  private final Institution institution;
  private final String name;
  private final EDurationUnit durationUnit;
  private final int durationCount;
  private boolean isAPC;

  private Program(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      EDurationUnit durationUnit,
      int durationCount) {
    this.id = id;
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
      int durationCount) {
    return new Program(id, institution, name, isAPC, durationUnit, durationCount);
  }

  public static Program toDomain(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      EDurationUnit durationUnit,
      int durationCount) {
    return new Program(id, institution, name, isAPC, durationUnit, durationCount);
  }
}
