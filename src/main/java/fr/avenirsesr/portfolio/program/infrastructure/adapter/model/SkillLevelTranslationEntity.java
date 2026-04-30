package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.DESCRIPTION_LENGTH;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "skill_level_translation",
    indexes = {
      @Index(name = "idx_skill_level_tr_skill_level", columnList = "skill_level_id"),
      @Index(name = "idx_skill_level_tr_skill_level_lang", columnList = "skill_level_id, language")
    })
@NoArgsConstructor
@Getter
@Setter
public class SkillLevelTranslationEntity extends TranslationEntity {
  @Column(nullable = false)
  private String name;

  @Column(length = DESCRIPTION_LENGTH)
  private String description;

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
    this.setCreatedAt(skillLevelEntity.getCreatedAt());
    this.setUpdatedAt(skillLevelEntity.getUpdatedAt());
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
