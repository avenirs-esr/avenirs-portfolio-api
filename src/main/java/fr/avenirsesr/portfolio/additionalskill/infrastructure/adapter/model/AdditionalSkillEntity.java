package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.Category;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private AdditionalSkillCategoryEntity category;

  private AdditionalSkillEntity(
      UUID id,
      PathSegmentsEmbeddable pathSegments,
      EAdditionalSkillType type,
      AdditionalSkillCategoryEntity category) {
    setId(id);
    this.pathSegments = pathSegments;
    this.type = type;
    this.category = category;
  }

  public static AdditionalSkillEntity of(
      UUID id,
      PathSegmentsEmbeddable pathSegments,
      EAdditionalSkillType type,
      AdditionalSkillCategoryEntity category) {
    return new AdditionalSkillEntity(id, pathSegments, type, category);
  }

  public static AdditionalSkillEntity create(
      PathSegmentsEmbeddable pathSegments,
      EAdditionalSkillType type,
      AdditionalSkillCategoryEntity category) {
    return new AdditionalSkillEntity(UUID.randomUUID(), pathSegments, type, category);
  }
}
