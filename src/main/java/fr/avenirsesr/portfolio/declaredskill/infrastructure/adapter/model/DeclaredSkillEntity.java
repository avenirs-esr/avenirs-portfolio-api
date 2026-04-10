package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.converter.PathSegmentsConverter;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "declared_skill", uniqueConstraints = @UniqueConstraint(columnNames = {"type"}))
@NoArgsConstructor
@Getter
@Setter
public class DeclaredSkillEntity extends AvenirsBaseEntity {

  @Column(name = "libelle")
  private String libelle;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private EExternalSkillType type;

  @Setter(AccessLevel.NONE)
  @Column(name = "path_segments")
  @Convert(converter = PathSegmentsConverter.class)
  private List<String> pathSegments;

  private DeclaredSkillEntity(
      UUID id, String libelle, EExternalSkillType type, List<String> pathSegments) {
    setId(id);
    this.libelle = libelle;
    this.type = type;
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }

  public static DeclaredSkillEntity of(
      UUID id, String libelle, EExternalSkillType type, List<String> pathSegments) {
    return new DeclaredSkillEntity(id, libelle, type, pathSegments);
  }

  public static DeclaredSkillEntity create(
      UUID id, String libelle, EExternalSkillType type, List<String> pathSegments) {
    return new DeclaredSkillEntity(id, libelle, type, pathSegments);
  }

  public void setPathSegments(List<String> pathSegments) {
    this.pathSegments = pathSegments != null ? pathSegments : List.of();
  }
}
