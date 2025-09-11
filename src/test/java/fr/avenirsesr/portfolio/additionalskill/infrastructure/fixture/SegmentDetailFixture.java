package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import java.util.UUID;

public class SegmentDetailFixture {
  private String code;
  private String libelle;

  private SegmentDetailFixture() {
    this.code = UUID.randomUUID().toString();
    this.libelle = "some segement detail libbelle";
  }

  public static SegmentDetailFixture create() {
    return new SegmentDetailFixture();
  }

  public SegmentDetailFixture withCode(String code) {
    this.code = code;
    return this;
  }

  public SegmentDetailFixture withLibelle(String libelle) {
    this.libelle = libelle;
    return this;
  }

  public SegmentDetail toModel() {
    return SegmentDetail.toDomain(code, libelle);
  }
}
