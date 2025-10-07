package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillCategory;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.seeder.domain.port.output.SharedDataGenerator;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.DataGeneratorProvider;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdditionalSkillFixture {
  private static final DataGeneratorProvider<SharedDataGenerator> dataGenerator =
      new DataGeneratorProvider<SharedDataGenerator>()
          .init(AdditionalSkillFixture.class, SharedDataGenerator.class);

  private UUID id;
  private String libelle;
  private String externalId;
  private AdditionalSkillCategory additionalSkillCategory;
  private EAdditionalSkillType type;
  private Instant createdAt;
  private Instant updatedAt;

  private AdditionalSkillFixture() {
    this.id = dataGenerator.with("id").uuid();
    this.additionalSkillCategory = null;
    this.type = EAdditionalSkillType.ROME4;
    this.libelle = "libelle";
    this.externalId = UUID.randomUUID().toString();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static AdditionalSkillFixture create() {
    return new AdditionalSkillFixture();
  }

  public AdditionalSkillFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public AdditionalSkillFixture withLibelle(String libelle) {
    this.libelle = libelle;
    return this;
  }

  public AdditionalSkillFixture withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

  public AdditionalSkillFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public AdditionalSkillFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public AdditionalSkillFixture withCategory(AdditionalSkillCategory additionalSkillCategory) {
    this.additionalSkillCategory = additionalSkillCategory;
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
    return AdditionalSkill.toDomain(
        id, libelle, externalId, additionalSkillCategory, type, createdAt, updatedAt);
  }
}
