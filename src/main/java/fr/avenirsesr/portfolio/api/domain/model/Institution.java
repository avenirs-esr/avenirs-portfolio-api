package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ENavigationField;
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

  private Set<ENavigationField> enabledFields;

  private Institution(UUID id, String name) {
    this.id = id;
    this.name = name;
  }

  public static Institution create(String name) {
    var institution = new Institution(UUID.randomUUID(), name);
    institution.setEnabledFields(Set.of(ENavigationField.values()));

    return institution;
  }

  public static Institution toDomain(UUID id, String name, Set<ENavigationField> enabledFields) {
    var institution = new Institution(id, name);
    institution.setEnabledFields(enabledFields);

    return institution;
  }
}
