package fr.avenirsesr.portfolio.additionalskill.domain.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdditionalSkill extends AvenirsBaseModel {
  private UUID externalSkillId;
  private String libelle;
  private EAdditionalSkillType type;

  @Setter(AccessLevel.NONE)
  private List<String> pathSegments;

  private AdditionalSkill(
      UUID id,
      UUID externalSkillId,
      String libelle,
      EAdditionalSkillType type,
      List<String> pathSegments,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.externalSkillId = externalSkillId;
    this.libelle = libelle;
    this.type = type;
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }

  public static AdditionalSkill create(
      UUID externalSkillId, String libelle, EAdditionalSkillType type, List<String> pathSegments) {
    Instant now = Instant.now();
    return new AdditionalSkill(
        UUID.randomUUID(), externalSkillId, libelle, type, pathSegments, now, now);
  }

  public static AdditionalSkill toDomain(
      UUID id,
      UUID externalSkillId,
      String libelle,
      EAdditionalSkillType type,
      List<String> pathSegments,
      Instant createdAt,
      Instant updatedAt) {
    return new AdditionalSkill(
        id, externalSkillId, libelle, type, pathSegments, createdAt, updatedAt);
  }

  public void setPathSegments(List<String> pathSegments) {
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }
}
