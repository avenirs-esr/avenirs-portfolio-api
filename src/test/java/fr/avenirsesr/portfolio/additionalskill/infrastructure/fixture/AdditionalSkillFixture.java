package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.fake.FakerProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdditionalSkillFixture {
  private static final FakerProvider faker = new FakerProvider();

  private UUID id;
  private PathSegments pathSegments;
  private EAdditionalSkillType type;

  private AdditionalSkillFixture() {
    this.id = UUID.fromString(faker.call().internet().uuid());
    this.pathSegments = PathSegmentsFixture.create().toModel();
    this.type = EAdditionalSkillType.fromValue("ROME4.0");
  }

  public static AdditionalSkillFixture create() {
    return new AdditionalSkillFixture();
  }

  public AdditionalSkillFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public AdditionalSkillFixture withPathSegments(PathSegments pathSegments) {
    this.pathSegments = pathSegments;
    return this;
  }

  public AdditionalSkillFixture withType(EAdditionalSkillType type) {
    this.type = type;
    return this;
  }

  public static List<AdditionalSkillFixture> create(int count) {
    List<AdditionalSkillFixture> additionalSkillFixtureList = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      additionalSkillFixtureList.add(create());
    }
    return additionalSkillFixtureList;
  }

  public AdditionalSkill toModel() {
    return AdditionalSkill.toDomain(id, pathSegments, type);
  }
}
