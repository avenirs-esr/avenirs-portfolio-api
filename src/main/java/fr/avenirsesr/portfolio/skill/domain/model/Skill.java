package fr.avenirsesr.portfolio.skill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skill extends AvenirsBaseModel {
  private SkillReferential referential;
  private String externalCode;
  private String title;
  private boolean assessable;
  private boolean archived;

  @Getter(AccessLevel.NONE)
  private String description;

  @Getter(AccessLevel.NONE)
  private Skill parent;

  private Skill(
      UUID id,
      SkillReferential referential,
      String externalCode,
      String title,
      String description,
      Skill parent,
      boolean assessable,
      boolean archived,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.referential = referential;
    this.externalCode = externalCode;
    this.title = title;
    this.description = description;
    this.parent = parent;
    this.assessable = assessable;
    this.archived = archived;
  }

  public static Skill create(
      UUID id,
      SkillReferential referential,
      String externalCode,
      String title,
      String description,
      Skill parent,
      boolean assessable) {
    Instant now = Instant.now();
    return new Skill(
        id, referential, externalCode, title, description, parent, assessable, false, now, now);
  }

  public static Skill toDomain(
      UUID id,
      SkillReferential referential,
      String externalCode,
      String title,
      String description,
      Skill parent,
      boolean assessable,
      boolean archived,
      Instant createdAt,
      Instant updatedAt) {
    return new Skill(
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

  public Optional<String> getDescription() {
    return Optional.ofNullable(description);
  }

  public Optional<Skill> getParent() {
    return Optional.ofNullable(parent);
  }

  public List<Skill> getAncestors() {
    List<Skill> ancestors = new ArrayList<>();
    Optional<Skill> current = getParent();
    while (current.isPresent()) {
      ancestors.add(current.get());
      current = current.get().getParent();
    }
    Collections.reverse(ancestors);
    return ancestors;
  }

  public List<String> getPathSegments() {
    return getAncestors().stream().map(Skill::getTitle).toList();
  }
}
