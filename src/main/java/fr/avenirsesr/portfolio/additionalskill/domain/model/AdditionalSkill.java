package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill extends AvenirsBaseModel {
  @Getter(AccessLevel.NONE)
  private AdditionalSkillCategory additionalSkillCategory;

  private String libelle;
  private String externalId;
  private EAdditionalSkillType type;

  private AdditionalSkill(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      AdditionalSkillCategory additionalSkillCategory,
      EAdditionalSkillType type,
      String libelle,
      String externalId) {
    super(id, createdAt, updatedAt);
    this.additionalSkillCategory = additionalSkillCategory;
    this.type = type;
    this.libelle = libelle;
    this.externalId = externalId;
  }

  public static AdditionalSkill create(
      String libelle,
      String externalId,
      AdditionalSkillCategory additionalSkillCategory,
      EAdditionalSkillType type) {
    Instant now = Instant.now();
    return new AdditionalSkill(
        UUID.randomUUID(), now, now, additionalSkillCategory, type, libelle, externalId);
  }

  public static AdditionalSkill toDomain(
      UUID id,
      String libelle,
      String externalId,
      AdditionalSkillCategory additionalSkillCategory,
      EAdditionalSkillType type,
      Instant createdAt,
      Instant updatedAt) {
    return new AdditionalSkill(
        id, createdAt, updatedAt, additionalSkillCategory, type, libelle, externalId);
  }

  public Optional<AdditionalSkillCategory> getAdditionalSkillCategory() {
    return Optional.ofNullable(additionalSkillCategory);
  }

  public List<AdditionalSkillCategory> getCategoryPath() {
    List<AdditionalSkillCategory> categories = new ArrayList<>();

    Optional<AdditionalSkillCategory> current = getAdditionalSkillCategory();
    while (current.isPresent()) {
      categories.add(current.get());
      current = current.get().getParent();
    }

    Collections.reverse(categories);
    return categories;
  }
}
