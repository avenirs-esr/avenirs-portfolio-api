package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "additional_skill")
@NoArgsConstructor
@Getter
@Setter
public class AdditionalSkillEntity extends AvenirsBaseEntity {

  @Embedded private PathSegmentsEmbeddable pathSegments;

  @Enumerated(EnumType.STRING)
  private EAdditionalSkillType type;

  private AdditionalSkillEntity(
      UUID id, PathSegmentsEmbeddable pathSegments, EAdditionalSkillType type) {
    setId(id);
    this.pathSegments = pathSegments;
    this.type = type;
  }

  public static AdditionalSkillEntity of(
      UUID id, PathSegmentsEmbeddable pathSegments, EAdditionalSkillType type) {
    return new AdditionalSkillEntity(id, pathSegments, type);
  }

  public static AdditionalSkillEntity create(
      PathSegmentsEmbeddable pathSegments, EAdditionalSkillType type) {
    return new AdditionalSkillEntity(UUID.randomUUID(), pathSegments, type);
  }
}
