package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class SegmentDetailEmbeddable {
  private String code;
  private String libelle;

  private SegmentDetailEmbeddable(String code, String libelle) {
    this.code = code;
    this.libelle = libelle;
  }

  public static SegmentDetailEmbeddable of(String code, String libelle) {
    return new SegmentDetailEmbeddable(code, libelle);
  }
}
