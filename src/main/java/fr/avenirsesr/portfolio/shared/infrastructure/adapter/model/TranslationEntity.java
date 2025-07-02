package fr.avenirsesr.portfolio.shared.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class TranslationEntity extends AvenirsBaseEntity {
  @Column(name = "language", nullable = false)
  protected ELanguage language;
}
