package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import java.util.UUID;

public interface AdditionalSkillMapper {
  static AdditionalSkill toDomain(CompetenceComplementaireDetaillee entity) {
    PathSegments pathSegments = PathSegmentsMapper.toDomain(entity);
    return AdditionalSkill.toDomain(
        UUID.randomUUID(), pathSegments, EAdditionalSkillType.fromValue(entity.type()));
  }
}
