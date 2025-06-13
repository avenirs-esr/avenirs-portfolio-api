package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skill_translation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SkillTranslationEntity extends TranslationEntity {
  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "skill_id", nullable = false)
  private SkillEntity skill;

  public SkillTranslationEntity(
      UUID uuid, ELanguage language, String name, SkillEntity skillEntity) {
    super();
    this.id = uuid;
    this.language = language;
    this.name = name;
    this.skill = skillEntity;
  }

  @Override
  public String toString() {
    return "SkillEntity[%s]".formatted(id);
  }
}
