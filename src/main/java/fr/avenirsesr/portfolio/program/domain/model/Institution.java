package fr.avenirsesr.portfolio.program.domain.model;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Institution extends AvenirsBaseModel {
  private final String name;
  private Set<EPortfolioType> enabledFields;

  private Institution(UUID id, String name, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.name = name;
  }

  public static Institution create(UUID id, String name) {
    var institution = new Institution(id, name, Instant.now(), Instant.now());
    institution.setEnabledFields(Set.of(EPortfolioType.values()));

    return institution;
  }

  public static Institution toDomain(
      UUID id,
      String name,
      Set<EPortfolioType> enabledFields,
      Instant createdAt,
      Instant updatedAt) {
    var institution = new Institution(id, name, createdAt, updatedAt);
    institution.setEnabledFields(enabledFields);

    return institution;
  }
}
