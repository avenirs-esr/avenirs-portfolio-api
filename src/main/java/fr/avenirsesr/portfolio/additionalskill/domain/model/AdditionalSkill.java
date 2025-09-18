package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill extends AvenirsBaseModel {

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
}
