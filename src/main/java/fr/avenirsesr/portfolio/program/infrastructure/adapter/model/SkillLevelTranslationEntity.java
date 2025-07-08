package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_level_translation")
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

  private SkillLevelTranslationEntity(
      UUID id,
      ELanguage eLanguage,
      String name,
      String description,
      SkillLevelEntity skillLevelEntity) {
    super();
    this.setId(id);
    this.language = eLanguage;
    this.name = name;
    this.description = description;
    this.skillLevel = skillLevelEntity;
  }

  public static SkillLevelTranslationEntity of(
      UUID id,
      ELanguage eLanguage,
      String name,
      String description,
      SkillLevelEntity skillLevelEntity) {
    return new SkillLevelTranslationEntity(id, eLanguage, name, description, skillLevelEntity);
  }

  @Override
  public String toString() {
    return "SkillLevelEntity[%s]".formatted(this.getId());
  }
}
