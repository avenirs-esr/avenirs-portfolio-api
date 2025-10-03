package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.Category;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.text.html.Option;

@Getter
@Setter
public class AdditionalSkill extends AvenirsBaseModel {
  @Getter(AccessLevel.NONE)
  private Category category;

  private PathSegments pathSegments;
  private EAdditionalSkillType type;

  private AdditionalSkill(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      PathSegments pathSegments,
      EAdditionalSkillType type) {
    super(id, createdAt, updatedAt);
    this.pathSegments = pathSegments;
    this.type = type;
  }

  public static AdditionalSkill create(PathSegments pathSegments, EAdditionalSkillType type) {
    Instant now = Instant.now();
    return new AdditionalSkill(UUID.randomUUID(), now, now, pathSegments, type);
  }

  public static AdditionalSkill toDomain(
      UUID id, PathSegments pathSegments, EAdditionalSkillType type) {
    Instant now = Instant.now();
    return new AdditionalSkill(id, now, now, pathSegments, type);
  }

  public Optional<Category> getCategory() {
    return Optional.ofNullable(category);
  }

  public List<Category> getCategoryPath() {
      List<Category> categories = new ArrayList<>();

      Optional<Category> current = getCategory();
      while (current.isPresent()) {
          categories.add(current.get());
          current = current.get().getParent();
      }

      Collections.reverse(categories);
      return categories;
  }
}
