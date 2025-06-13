package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "program_translation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id", nullable = false)
  private ProgramEntity program;

  public ProgramTranslationEntity(
      UUID id, ELanguage language, String name, ProgramEntity programEntity) {
    super();
    this.id = id;
    this.language = language;
    this.name = name;
    this.program = programEntity;
  }
}
