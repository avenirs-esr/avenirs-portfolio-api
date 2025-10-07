package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "additional_skill")
@NoArgsConstructor
@Getter
@Setter
public class AdditionalSkillEntity extends AvenirsBaseEntity {

  @Column(nullable = false, name = "external_id")
  private String externalId;

  @Column(nullable = false)
  private String libelle;

  @Enumerated(EnumType.STRING)
  private EAdditionalSkillType type;

  @Getter(AccessLevel.NONE)
  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private AdditionalSkillCategoryEntity additionalSkillCategory;

  private AdditionalSkillEntity(
      UUID id,
      String externalId,
      String libelle,
      EAdditionalSkillType type,
      AdditionalSkillCategoryEntity additionalSkillCategory) {
    setId(id);
    this.type = type;
    this.additionalSkillCategory = additionalSkillCategory;
    this.externalId = externalId;
    this.libelle = libelle;
  }

  public static AdditionalSkillEntity of(
      UUID id,
      String externalId,
      String libelle,
      EAdditionalSkillType type,
      AdditionalSkillCategoryEntity additionalSkillCategory) {
    return new AdditionalSkillEntity(id, externalId, libelle, type, additionalSkillCategory);
  }

  public static AdditionalSkillEntity create(
      String externalId,
      String libelle,
      EAdditionalSkillType type,
      AdditionalSkillCategoryEntity additionalSkillCategory) {
    return new AdditionalSkillEntity(
        UUID.randomUUID(), externalId, libelle, type, additionalSkillCategory);
  }

  public Optional<AdditionalSkillCategoryEntity> getAdditionalSkillCategory() {
    return Optional.ofNullable(additionalSkillCategory);
  }
}
