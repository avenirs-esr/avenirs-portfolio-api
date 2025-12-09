package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.converter.PathSegmentsConverter;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "additional_skill")
@NoArgsConstructor
@Getter
@Setter
public class AdditionalSkillEntity extends AvenirsBaseEntity {

  @Column(nullable = false, name = "external_skill_id")
  private UUID externalSkillId;

  @Column(name = "libelle")
  private String libelle;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private EAdditionalSkillType type;

  @Setter(AccessLevel.NONE)
  @Column(name = "path_segments")
  @Convert(converter = PathSegmentsConverter.class)
  private List<String> pathSegments;

  private AdditionalSkillEntity(
      UUID id,
      UUID externalSkillId,
      String libelle,
      EAdditionalSkillType type,
      List<String> pathSegments) {
    setId(id);
    this.externalSkillId = externalSkillId;
    this.libelle = libelle;
    this.type = type;
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }

  public static AdditionalSkillEntity of(
      UUID id,
      UUID externalSkillId,
      String libelle,
      EAdditionalSkillType type,
      List<String> pathSegments) {
    return new AdditionalSkillEntity(id, externalSkillId, libelle, type, pathSegments);
  }

  public static AdditionalSkillEntity create(
      UUID externalSkillId, String libelle, EAdditionalSkillType type, List<String> pathSegments) {
    return new AdditionalSkillEntity(
        UUID.randomUUID(), externalSkillId, libelle, type, pathSegments);
  }

  public void setPathSegments(List<String> pathSegments) {
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }
}
