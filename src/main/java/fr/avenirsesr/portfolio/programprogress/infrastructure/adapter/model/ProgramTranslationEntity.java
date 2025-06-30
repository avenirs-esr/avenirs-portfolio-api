package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program_translation")
@NoArgsConstructor
@Getter
@Setter
public class ProgramTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id", nullable = false)
  private ProgramEntity program;

  private ProgramTranslationEntity(
      UUID id, ELanguage language, String name, ProgramEntity programEntity) {
    super();
    this.setId(id);
    this.language = language;
    this.name = name;
    this.program = programEntity;
  }

  public static ProgramTranslationEntity of(
      UUID id, ELanguage language, String name, ProgramEntity programEntity) {
    return new ProgramTranslationEntity(id, language, name, programEntity);
  }
}
