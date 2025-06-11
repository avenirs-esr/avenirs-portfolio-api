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
public class ProgramTranslationEntity {
  @Id private UUID id;

  @Column(name = "language", nullable = false)
  private ELanguage language;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id", nullable = false)
  private ProgramEntity program;
}
