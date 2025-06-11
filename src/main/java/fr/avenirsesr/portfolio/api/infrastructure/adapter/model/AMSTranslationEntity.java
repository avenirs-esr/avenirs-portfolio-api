package fr.avenirsesr.portfolio.api.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ams_translation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AMSTranslationEntity {
  @Id private UUID id;

  @Column(name = "language", nullable = false)
  private ELanguage language;

  @Column(nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ams_id", nullable = false)
  private AMSEntity ams;
}
