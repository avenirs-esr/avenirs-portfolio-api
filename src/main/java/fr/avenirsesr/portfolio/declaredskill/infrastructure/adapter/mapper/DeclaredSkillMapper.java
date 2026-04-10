package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;

public class DeclaredSkillMapper implements Mapper<DeclaredSkillEntity, DeclaredSkill> {

  public static final DeclaredSkillMapper INSTANCE = new DeclaredSkillMapper();

  @Override
  public DeclaredSkill toDomain(DeclaredSkillEntity entity) {
    return DeclaredSkill.toDomain(
        entity.getId(),
        entity.getLibelle(),
        entity.getType(),
        entity.getPathSegments(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public DeclaredSkillEntity fromDomain(DeclaredSkill domain) {
    return DeclaredSkillEntity.of(
        domain.getId(), domain.getLibelle(), domain.getType(), domain.getPathSegments());
  }
}
