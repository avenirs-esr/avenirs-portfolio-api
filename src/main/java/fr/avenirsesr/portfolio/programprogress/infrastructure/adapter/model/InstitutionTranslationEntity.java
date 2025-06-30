package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "institution_translation")
@NoArgsConstructor
@Getter
@Setter
public class InstitutionTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "institution_id", nullable = false)
  private InstitutionEntity institution;

  private InstitutionTranslationEntity(
      UUID id, ELanguage language, String name, InstitutionEntity institutionEntity) {
    super();
    this.setId(id);
    this.language = language;
    this.name = name;
    this.institution = institutionEntity;
  }

  public static InstitutionTranslationEntity of(
      UUID id, ELanguage language, String name, InstitutionEntity institutionEntity) {
    return new InstitutionTranslationEntity(id, language, name, institutionEntity);
  }
}
