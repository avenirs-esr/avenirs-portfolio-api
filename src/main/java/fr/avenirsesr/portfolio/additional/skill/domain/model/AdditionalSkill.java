package fr.avenirsesr.portfolio.additional.skill.domain.model;

import fr.avenirsesr.portfolio.additional.skill.domain.model.enums.EAdditionalSkillType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill {
  private String id;
  private PathSegments pathSegments;
  private EAdditionalSkillType type;

  private AdditionalSkill(String id) {
    this.id = id;
  }

  public static AdditionalSkill create(
      String id, PathSegments pathSegments, EAdditionalSkillType type) {
    AdditionalSkill additionalSkill = new AdditionalSkill(id);
    additionalSkill.setPathSegments(pathSegments);
    additionalSkill.setType(type);
    return additionalSkill;
  }

  public static AdditionalSkill toDomain(
      String id, PathSegments pathSegments, EAdditionalSkillType type) {
    return create(id, pathSegments, type);
  }
}
