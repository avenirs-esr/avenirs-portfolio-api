package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additional.skill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;

public interface AdditionalSkillMapper {
  static AdditionalSkill toDomain(CompetenceComplementaireDetaillee entity) {
    PathSegments pathSegments = PathSegmentsMapper.toDomain(entity);
    return AdditionalSkill.toDomain(entity.getCode(), pathSegments, entity.getType());
  }

  static CompetenceComplementaireDetaillee fromDomain(AdditionalSkill additionalSkill) {
    CompetenceComplementaireDetaillee entity = new CompetenceComplementaireDetaillee();
    entity.setCode(additionalSkill.getId());
    entity.setType(additionalSkill.getType());
    entity.setLibelle(additionalSkill.getPathSegments().getSkill().getLibelle());
    return entity;
  }
}
