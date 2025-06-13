package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level_translation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelTranslationEntity extends TranslationEntity {
  @Column(nullable = false)
  private String name;

  @Column() private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_level_id", nullable = false)
  private SkillLevelEntity skillLevel;

  public SkillLevelTranslationEntity(
      UUID uuid,
      ELanguage eLanguage,
      String name,
      String description,
      SkillLevelEntity skillLevelEntity) {
    super();
    this.id = uuid;
    this.language = eLanguage;
    this.name = name;
    this.description = description;
    this.skillLevel = skillLevelEntity;
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(id);
  }
}
