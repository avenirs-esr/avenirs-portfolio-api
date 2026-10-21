package fr.avenirsesr.portfolio.skill.infrastructure.fixture;

import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import java.time.Instant;
import java.util.UUID;

public class SkillFixture {
  private UUID id;
  private SkillReferential referential;
  private String externalCode;
  private String title;
  private String description;
  private Skill parent;
  private boolean assessable;
  private boolean archived;
  private Instant createdAt;
  private Instant updatedAt;

  private SkillFixture() {
    this.id = UUID.randomUUID();
    this.referential = SkillReferentialFixture.create().toModel();
    this.externalCode = "CODE";
    this.title = "Test Skill";
    this.description = null;
    this.parent = null;
    this.assessable = true;
    this.archived = false;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static SkillFixture create() {
    return new SkillFixture();
  }

  public SkillFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillFixture withReferential(SkillReferential referential) {
    this.referential = referential;
    return this;
  }

  public SkillFixture withExternalCode(String externalCode) {
    this.externalCode = externalCode;
    return this;
  }

  public SkillFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public SkillFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public SkillFixture withParent(Skill parent) {
    this.parent = parent;
    if (parent != null) {
      this.referential = parent.getReferential();
    }
    return this;
  }

  public SkillFixture withAssessable(boolean assessable) {
    this.assessable = assessable;
    return this;
  }

  public SkillFixture withArchived(boolean archived) {
    this.archived = archived;
    return this;
  }

  public SkillFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public SkillFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public Skill toModel() {
    return Skill.toDomain(
        id,
        referential,
        externalCode,
        title,
        description,
        parent,
        assessable,
        archived,
        createdAt,
        updatedAt);
  }
}
