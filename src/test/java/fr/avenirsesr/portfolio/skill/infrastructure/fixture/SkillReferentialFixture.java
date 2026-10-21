package fr.avenirsesr.portfolio.skill.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import java.time.Instant;
import java.util.UUID;

public class SkillReferentialFixture {
  private UUID id;
  private ESkillReferentialType type;
  private String externalId;
  private String title;
  private UUID institutionId;
  private Instant createdAt;
  private Instant updatedAt;

  private SkillReferentialFixture() {
    this.id = UUID.randomUUID();
    this.type = ESkillReferentialType.ROME4;
    this.externalId = "ROME4";
    this.title = "ROME4";
    this.institutionId = null;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static SkillReferentialFixture create() {
    return new SkillReferentialFixture();
  }

  public SkillReferentialFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillReferentialFixture withType(ESkillReferentialType type) {
    this.type = type;
    this.externalId = type.name();
    this.title = type.name();
    return this;
  }

  public SkillReferentialFixture withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

  public SkillReferentialFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public SkillReferentialFixture withInstitutionId(UUID institutionId) {
    this.institutionId = institutionId;
    return this;
  }

  public SkillReferentialFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SkillReferentialFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public SkillReferential toModel() {
    return SkillReferential.toDomain(
        id, type, externalId, title, institutionId, createdAt, updatedAt);
  }
}
