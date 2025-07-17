package fr.avenirsesr.portfolio.additional.skill.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill {
  private String id;
  private PathSegments pathSegments;
  private String type;

  private AdditionalSkill(String id) {
    this.id = id;
  }

  public static AdditionalSkill create(String id, PathSegments pathSegments, String type) {
    AdditionalSkill additionalSkill = new AdditionalSkill(id);
    additionalSkill.setPathSegments(pathSegments);
    additionalSkill.setType(type);
    return additionalSkill;
  }

  public static AdditionalSkill toDomain(String id, PathSegments pathSegments, String type) {
    return create(id, pathSegments, type);
  }
}
