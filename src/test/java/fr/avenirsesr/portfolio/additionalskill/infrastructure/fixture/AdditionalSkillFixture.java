package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdditionalSkillFixture {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(AdditionalSkillFixture.class, SharedDataGenerator.class);

  private UUID id;
  private PathSegments pathSegments;
  private EAdditionalSkillType type;

  private AdditionalSkillFixture() {
    this.id = dataGenerator.with("id").uuid();
    this.pathSegments = PathSegmentsFixture.create().toModel();
    this.type = EAdditionalSkillType.ROME4;
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
