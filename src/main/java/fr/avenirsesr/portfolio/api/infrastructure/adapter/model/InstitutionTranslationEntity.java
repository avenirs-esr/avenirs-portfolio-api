package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution_translation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InstitutionTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "institution_id", nullable = false)
  private InstitutionEntity institution;

  public InstitutionTranslationEntity(
      UUID uuid, ELanguage language, String name, InstitutionEntity institutionEntity) {
    super();
    this.id = uuid;
    this.language = language;
    this.name = name;
    this.institution = institutionEntity;
  }
}
