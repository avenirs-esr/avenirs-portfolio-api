package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill extends AvenirsBaseModel {

  private PathSegments pathSegments;
  private EAdditionalSkillType type;

  private AdditionalSkill(UUID id, Instant createdAt, Instant updatedAt) {
    super(id, createdAt, updatedAt);
  }

  public static AdditionalSkill create(
      UUID id, PathSegments pathSegments, EAdditionalSkillType type) {
    AdditionalSkill additionalSkill = new AdditionalSkill(id, Instant.now(), Instant.now());
    additionalSkill.setPathSegments(pathSegments);
    additionalSkill.setType(type);
    return additionalSkill;
  }

  public static AdditionalSkill toDomain(
      UUID id, PathSegments pathSegments, EAdditionalSkillType type) {
    return create(id, pathSegments, type);
  }
}
