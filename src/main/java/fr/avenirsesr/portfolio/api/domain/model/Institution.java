package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Institution {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  @Setter(AccessLevel.NONE)
  private final String name;

  private final ELanguage language;

  private Set<EPortfolioType> enabledFields;

  private Institution(UUID id, String name, ELanguage language) {
    this.id = id;
    this.name = name;
    this.language = language;
  }

  public static Institution create(UUID id, String name, ELanguage language) {
    var institution = new Institution(id, name, language);
    institution.setEnabledFields(Set.of(EPortfolioType.values()));

    return institution;
  }

  public static Institution toDomain(
      UUID id, String name, Set<EPortfolioType> enabledFields, ELanguage language) {
    var institution = new Institution(id, name, language);
    institution.setEnabledFields(enabledFields);

    return institution;
  }
}
