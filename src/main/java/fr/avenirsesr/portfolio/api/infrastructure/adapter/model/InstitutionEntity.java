package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import jakarta.persistence.*;
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
public class InstitutionEntity extends AvenirsBaseEntity {
  @Column(name = "enabled_fields", nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private String enabledFieldsRaw;

  @OneToMany(
      mappedBy = "institution",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<InstitutionTranslationEntity> translations =
      new HashSet<>(); // TODO: Remove this SET and get it in queries

  public InstitutionEntity(UUID id, Set<EPortfolioType> enabledFields) {
    this.setId(id);
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
