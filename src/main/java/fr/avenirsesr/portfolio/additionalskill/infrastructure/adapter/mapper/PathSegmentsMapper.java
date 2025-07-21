package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;

public interface PathSegmentsMapper {
  static PathSegments toDomain(CompetenceComplementaireDetaillee entity) {
    SegmentDetail issue =
        SegmentDetailMapper.toDomain(
            entity.macroCompetence().objectif().enjeu().code(),
            entity.macroCompetence().objectif().enjeu().libelle());

    SegmentDetail target =
        SegmentDetailMapper.toDomain(
            entity.macroCompetence().objectif().code(),
            entity.macroCompetence().objectif().libelle());

    SegmentDetail macroSkill =
        SegmentDetailMapper.toDomain(
            entity.macroCompetence().code(), entity.macroCompetence().libelle());

    SegmentDetail skill = SegmentDetailMapper.toDomain(entity.code(), entity.libelle());

    return PathSegments.toDomain(issue, target, macroSkill, skill);
  }
}
