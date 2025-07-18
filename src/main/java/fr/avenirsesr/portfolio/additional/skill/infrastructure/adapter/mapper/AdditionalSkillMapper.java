package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additional.skill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additional.skill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;

public interface AdditionalSkillMapper {
  static AdditionalSkill toDomain(CompetenceComplementaireDetaillee entity) {
    PathSegments pathSegments = PathSegmentsMapper.toDomain(entity);
    return AdditionalSkill.toDomain(
        entity.code(), pathSegments, EAdditionalSkillType.fromValue(entity.type()));
  }
}
