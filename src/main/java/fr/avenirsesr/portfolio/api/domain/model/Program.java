package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EDurationUnit;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Program {
  private final UUID id;
  private final Institution institution;
  private final String name;
  private final ELanguage language;
  private final EDurationUnit durationUnit;
  private final int durationCount;
  private boolean isAPC;

  private Program(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      ELanguage language,
      EDurationUnit durationUnit,
      int durationCount) {
    this.id = id;
    this.institution = institution;
    this.name = name;
    this.isAPC = isAPC;
    this.language = language;
    this.durationUnit = durationUnit;
    this.durationCount = durationCount;
  }

  public static Program create(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      ELanguage language,
      EDurationUnit durationUnit,
      int durationCount) {
    return new Program(id, institution, name, isAPC, language, durationUnit, durationCount);
  }

  public static Program toDomain(
      UUID id,
      Institution institution,
      String name,
      boolean isAPC,
      ELanguage language,
      EDurationUnit durationUnit,
      int durationCount) {
    return new Program(id, institution, name, isAPC, language, durationUnit, durationCount);
  }
}
