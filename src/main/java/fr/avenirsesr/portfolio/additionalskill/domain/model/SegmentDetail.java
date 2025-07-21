package fr.avenirsesr.portfolio.additionalskill.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SegmentDetail {
  private String code;
  private String libelle;

  private SegmentDetail(String code, String libelle) {
    this.code = code;
    this.libelle = libelle;
  }

  public static SegmentDetail create(String code, String libelle) {
    return new SegmentDetail(code, libelle);
  }

  public static SegmentDetail toDomain(String code, String libelle) {
    return create(code, libelle);
  }
}
