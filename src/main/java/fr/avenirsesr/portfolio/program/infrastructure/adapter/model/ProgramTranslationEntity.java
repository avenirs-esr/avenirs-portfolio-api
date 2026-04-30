package fr.avenirsesr.portfolio.program.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "program_translation",
    indexes = {
      @Index(name = "idx_program_tr_program", columnList = "program_id"),
      @Index(name = "idx_program_tr_program_lang", columnList = "program_id, language")
    })
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
    this.setCreatedAt(programEntity.getCreatedAt());
    this.setUpdatedAt(programEntity.getUpdatedAt());
    this.language = language;
    this.name = name;
    this.program = programEntity;
  }

  public static ProgramTranslationEntity of(
      UUID id, ELanguage language, String name, ProgramEntity programEntity) {
    return new ProgramTranslationEntity(id, language, name, programEntity);
  }
}
