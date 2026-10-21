package fr.avenirsesr.portfolio.skill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillReferential extends AvenirsBaseModel {
  private ESkillReferentialType type;
  private String externalId;
  private String title;

  @Getter(AccessLevel.NONE)
  private UUID institutionId;

  private SkillReferential(
      UUID id,
      ESkillReferentialType type,
      String externalId,
      String title,
      UUID institutionId,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.type = type;
    this.externalId = externalId;
    this.title = title;
    this.institutionId = institutionId;
  }

  public static SkillReferential create(
      ESkillReferentialType type, String externalId, String title, UUID institutionId) {
    Instant now = Instant.now();
    return new SkillReferential(
        UUID.randomUUID(), type, externalId, title, institutionId, now, now);
  }

  public static SkillReferential toDomain(
      UUID id,
      ESkillReferentialType type,
      String externalId,
      String title,
      UUID institutionId,
      Instant createdAt,
      Instant updatedAt) {
    return new SkillReferential(id, type, externalId, title, institutionId, createdAt, updatedAt);
  }

  public Optional<UUID> getInstitutionId() {
    return Optional.ofNullable(institutionId);
  }

  public boolean isNational() {
    return institutionId == null;
  }
}
