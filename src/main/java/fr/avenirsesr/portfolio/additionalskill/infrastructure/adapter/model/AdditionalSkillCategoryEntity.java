package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "additional_skill_category")
public class AdditionalSkillCategoryEntity extends AvenirsBaseEntity {
  @Column(nullable = false)
  private String libelle;

  @Column
  @Enumerated(EnumType.STRING)
  private EAdditionalSkillCategoryType type;

  @Getter(AccessLevel.NONE)
  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private AdditionalSkillCategoryEntity parent;

  private AdditionalSkillCategoryEntity(
      UUID id,
      String libelle,
      EAdditionalSkillCategoryType type,
      AdditionalSkillCategoryEntity parent) {
    setId(id);
    this.libelle = libelle;
    this.type = type;
    this.parent = parent;
  }

  public static AdditionalSkillCategoryEntity of(
      UUID id,
      String libelle,
      EAdditionalSkillCategoryType type,
      AdditionalSkillCategoryEntity parent) {
    return new AdditionalSkillCategoryEntity(id, libelle, type, parent);
  }

  public static AdditionalSkillCategoryEntity create(
      String libelle, EAdditionalSkillCategoryType type, AdditionalSkillCategoryEntity parent) {
    return new AdditionalSkillCategoryEntity(UUID.randomUUID(), libelle, type, parent);
  }

  public Optional<AdditionalSkillCategoryEntity> getParent() {
    return Optional.ofNullable(parent);
  }
}
