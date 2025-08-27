package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.UuidV7Generator;

public class SegmentDetailFixture {
  private static final FakerProvider faker = new FakerProvider().init(SegmentDetailFixture.class);

  private String code;
  private String libelle;

  private SegmentDetailFixture() {
    UuidGenerator uuidGenerator = new UuidV7Generator();
    this.code = uuidGenerator.generate().toString();
    this.libelle = faker.call("libelle").name().title();
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
