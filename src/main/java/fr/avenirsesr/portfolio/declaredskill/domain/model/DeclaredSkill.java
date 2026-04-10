package fr.avenirsesr.portfolio.declaredskill.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeclaredSkill extends AvenirsBaseModel {
  private String libelle;
  private EExternalSkillType type;

  @Setter(AccessLevel.NONE)
  private List<String> pathSegments;

  private DeclaredSkill(
      UUID id,
      String libelle,
      EExternalSkillType type,
      List<String> pathSegments,
      Instant createdAt,
      Instant updatedAt) {
    super(id, createdAt, updatedAt);
    this.libelle = libelle;
    this.type = type;
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }

  public static DeclaredSkill create(
      UUID id, String libelle, EExternalSkillType type, List<String> pathSegments) {
    Instant now = Instant.now();
    return new DeclaredSkill(id, libelle, type, pathSegments, now, now);
  }

  public static DeclaredSkill toDomain(
      UUID id,
      String libelle,
      EExternalSkillType type,
      List<String> pathSegments,
      Instant createdAt,
      Instant updatedAt) {
    return new DeclaredSkill(id, libelle, type, pathSegments, createdAt, updatedAt);
  }

  public void setPathSegments(List<String> pathSegments) {
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }
}
