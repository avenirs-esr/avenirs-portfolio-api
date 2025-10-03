package fr.avenirsesr.portfolio.additionalskill;

import fr.avenirsesr.portfolio.additionalskill.domain.EAdditionalSkillCategoryType;
import lombok.Getter;

import java.util.Optional;

public class Category {
  @Getter private final String libelle;
  @Getter private final EAdditionalSkillCategoryType type;
  private final Category parent;

  private Category(String libelle, Category parent, EAdditionalSkillCategoryType type) {
    this.libelle = libelle;
    this.parent = parent;
    this.type = type;
  }

  public static Category of(String libelle, Category parent, EAdditionalSkillCategoryType type) {
    return new Category(libelle, parent, type);
  }

  public static Category toDomain(
      String libelle, Category parent, EAdditionalSkillCategoryType type) {
    return new Category(libelle, parent, type);
  }

  public Optional<Category> getParent() {
    return Optional.ofNullable(parent);
  }
}
