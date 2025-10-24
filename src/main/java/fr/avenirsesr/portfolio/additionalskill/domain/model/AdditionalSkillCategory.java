package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillCategoryType;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;

public class AdditionalSkillCategory extends AvenirsBaseModel {
  @Getter private final String libelle;
  @Getter private final EAdditionalSkillCategoryType type;
  private final AdditionalSkillCategory parent;

  private AdditionalSkillCategory(
      UUID id,
      String libelle,
      AdditionalSkillCategory parent,
      EAdditionalSkillCategoryType type,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.libelle = libelle;
    this.parent = parent;
    this.type = type;
  }

  public static AdditionalSkillCategory of(
      String libelle, AdditionalSkillCategory parent, EAdditionalSkillCategoryType type) {
    return new AdditionalSkillCategory(
        UUID.randomUUID(), libelle, parent, type, Instant.now(), Instant.now());
  }

  public static AdditionalSkillCategory toDomain(
      UUID id,
      String libelle,
      AdditionalSkillCategory parent,
      EAdditionalSkillCategoryType type,
      Instant createdAt,
      Instant updatedAt) {
    return new AdditionalSkillCategory(id, libelle, parent, type, createdAt, updatedAt);
  }

  public Optional<AdditionalSkillCategory> getParent() {
    return Optional.ofNullable(parent);
  }

  public long uniqHash() {
    return libelle.hashCode() + type.hashCode() + (parent == null ? 0 : parent.hashCode());
  }
}
