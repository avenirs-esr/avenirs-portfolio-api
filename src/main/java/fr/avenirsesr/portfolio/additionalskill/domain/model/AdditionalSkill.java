package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill extends AvenirsBaseModel {

  private PathSegments pathSegments;
  private EAdditionalSkillType type;

  private AdditionalSkill(UUID id, PathSegments pathSegments, EAdditionalSkillType type) {
    super(id);
    this.pathSegments = pathSegments;
    this.type = type;
  }

  public static AdditionalSkill create(PathSegments pathSegments, EAdditionalSkillType type) {
    return new AdditionalSkill(UUID.randomUUID(), pathSegments, type);
  }

  public static AdditionalSkill toDomain(
      UUID id, PathSegments pathSegments, EAdditionalSkillType type) {
    return new AdditionalSkill(id, pathSegments, type);
  }
}
