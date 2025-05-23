package fr.avenirsesr.portfolio.api.domain.model;

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

  private Set<EPortfolioType> enabledFields;

  private Institution(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static Institution create(String name) {
    var institution = new Institution(UUID.randomUUID(), name);
    institution.setEnabledFields(Set.of(EPortfolioType.values()));

    return institution;
  }

  public static Institution toDomain(UUID id, String name, Set<EPortfolioType> enabledFields) {
    var institution = new Institution(id, name);
    institution.setEnabledFields(enabledFields);

    return institution;
  }
}
