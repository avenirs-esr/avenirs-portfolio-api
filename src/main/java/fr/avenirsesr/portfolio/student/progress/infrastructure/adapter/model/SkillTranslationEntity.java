package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_translation")
@NoArgsConstructor
@Getter
@Setter
public class SkillTranslationEntity extends TranslationEntity {
  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id", nullable = false)
  private SkillEntity skill;

  private SkillTranslationEntity(
      UUID id, ELanguage language, String name, SkillEntity skillEntity) {
    super();
    this.setId(id);
    this.language = language;
    this.name = name;
    this.skill = skillEntity;
  }

  public static SkillTranslationEntity of(
      UUID id, ELanguage language, String name, SkillEntity skillEntity) {
    return new SkillTranslationEntity(id, language, name, skillEntity);
  }

  @Override
  public String toString() {
    return "SkillEntity[%s]".formatted(this.getId());
  }
}
