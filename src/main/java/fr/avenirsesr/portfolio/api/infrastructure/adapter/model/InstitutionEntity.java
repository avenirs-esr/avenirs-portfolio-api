package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InstitutionEntity {
  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(name = "enabled_fields", nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String enabledFieldsRaw;

  public InstitutionEntity(UUID id, String name, Set<EPortfolioType> enabledFields) {
    this.id = id;
    this.name = name;
    setEnabledFields(enabledFields);
  }

  @Transient
  public Set<EPortfolioType> getEnabledFields() {
    if (enabledFieldsRaw == null || enabledFieldsRaw.isBlank()) {
      return new HashSet<>();
    }
    return Arrays.stream(enabledFieldsRaw.split(","))
        .map(String::trim)
        .map(EPortfolioType::valueOf)
        .collect(Collectors.toSet());
  }

  public void setEnabledFields(Set<EPortfolioType> enabledFields) {
    this.enabledFieldsRaw = enabledFields.stream().map(Enum::name).collect(Collectors.joining(","));
  }
}
