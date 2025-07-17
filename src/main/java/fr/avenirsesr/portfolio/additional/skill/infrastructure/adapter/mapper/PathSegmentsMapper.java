package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additional.skill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additional.skill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;

public interface PathSegmentsMapper {
  static PathSegments toDomain(CompetenceComplementaireDetaillee entity) {
    SegmentDetail issue =
        SegmentDetailMapper.toDomain(
            entity.getMacroCompetence().getObjectif().getEnjeu().getCode(),
            entity.getMacroCompetence().getObjectif().getEnjeu().getLibelle());

    SegmentDetail target =
        SegmentDetailMapper.toDomain(
            entity.getMacroCompetence().getObjectif().getCode(),
            entity.getMacroCompetence().getObjectif().getLibelle());

    SegmentDetail macroSkill =
        SegmentDetailMapper.toDomain(
            entity.getMacroCompetence().getCode(), entity.getMacroCompetence().getLibelle());

    SegmentDetail skill = SegmentDetailMapper.toDomain(entity.getCode(), entity.getLibelle());

    return PathSegments.create(issue, target, macroSkill, skill);
  }
}
