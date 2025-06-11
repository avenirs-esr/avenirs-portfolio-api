package fr.avenirsesr.portfolio.api.domain.model;

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
  private boolean isAPC;

  private Program(
      UUID id, Institution institution, String name, boolean isAPC, ELanguage language) {
    this.id = id;
    this.institution = institution;
    this.name = name;
    this.isAPC = isAPC;
    this.language = language;
  }

  public static Program create(
      UUID id, Institution institution, String name, boolean isAPC, ELanguage language) {
    return new Program(id, institution, name, isAPC, language);
  }

  public static Program toDomain(
      UUID id, Institution institution, String name, boolean isAPC, ELanguage language) {
    return new Program(id, institution, name, isAPC, language);
  }
}
