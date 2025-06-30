package fr.avenirsesr.portfolio.ams.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.TranslationEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ams_translation")
@NoArgsConstructor
@Getter
@Setter
public class AMSTranslationEntity extends TranslationEntity {

  @Column(nullable = false)
  private String title;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ams_id", nullable = false)
  private AMSEntity ams;

  private AMSTranslationEntity(UUID id, ELanguage language, String title, AMSEntity ams) {
    super();
    this.setId(id);
    this.language = language;
    this.title = title;
    this.ams = ams;
  }

  public static AMSTranslationEntity of(UUID id, ELanguage language, String title, AMSEntity ams) {
    return new AMSTranslationEntity(id, language, title, ams);
  }
}
