package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.PathSegments;
import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Competence;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.CompetenceComplementaireDetaillee;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.PathSegmentsEmbeddable;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.SegmentDetailEmbeddable;

public interface AdditionalSkillMapper {
  static AdditionalSkill toDomain(CompetenceComplementaireDetaillee entity) {
    PathSegments pathSegments = PathSegmentsMapper.toDomain(entity);
    return AdditionalSkill.toDomain(
        entity.id(), pathSegments, EAdditionalSkillType.fromValue(entity.type()));
  }

  static AdditionalSkill toDomain(AdditionalSkillEntity entity) {
    return AdditionalSkill.toDomain(
        entity.getId(),
        PathSegments.toDomain(
            SegmentDetail.toDomain(
                entity.getPathSegments().getSkill().getCode(),
                entity.getPathSegments().getSkill().getLibelle()),
            SegmentDetail.toDomain(
                entity.getPathSegments().getMacroSkill().getCode(),
                entity.getPathSegments().getMacroSkill().getLibelle()),
            SegmentDetail.toDomain(
                entity.getPathSegments().getTarget().getCode(),
                entity.getPathSegments().getTarget().getLibelle()),
            SegmentDetail.toDomain(
                entity.getPathSegments().getIssue().getCode(),
                entity.getPathSegments().getIssue().getLibelle()),
            SegmentDetail.toDomain(
                entity.getPathSegments().getDomain().getCode(),
                entity.getPathSegments().getDomain().getLibelle())),
        entity.getType());
  }

  static AdditionalSkill createToDomain(Competence competence) {
    return AdditionalSkill.create(
        PathSegments.toDomain(
            SegmentDetail.toDomain(competence.getCode(), competence.getLibelle()),
            SegmentDetail.toDomain(
                competence.getMacroCompetence().getCode(),
                competence.getMacroCompetence().getLibelle()),
            SegmentDetail.toDomain(
                competence.getMacroCompetence().getObjectif().getCode(),
                competence.getMacroCompetence().getObjectif().getLibelle()),
            SegmentDetail.toDomain(
                competence.getMacroCompetence().getObjectif().getEnjeu().getCode(),
                competence.getMacroCompetence().getObjectif().getEnjeu().getLibelle()),
            SegmentDetail.toDomain(
                competence
                    .getMacroCompetence()
                    .getObjectif()
                    .getEnjeu()
                    .getDomaineCompetence()
                    .getCode(),
                competence
                    .getMacroCompetence()
                    .getObjectif()
                    .getEnjeu()
                    .getDomaineCompetence()
                    .getLibelle())),
        EAdditionalSkillType.ROME4);
  }

  static AdditionalSkillEntity fromDomain(AdditionalSkill domain) {
    return AdditionalSkillEntity.of(
        domain.getId(), toEmbeddable(domain.getPathSegments()), domain.getType());
  }

  static PathSegmentsEmbeddable toEmbeddable(PathSegments domain) {
    return PathSegmentsEmbeddable.of(
        toEmbeddable(domain.getSkill()),
        toEmbeddable(domain.getMacroSkill()),
        toEmbeddable(domain.getTarget()),
        toEmbeddable(domain.getIssue()),
        toEmbeddable(domain.getDomain()));
  }

  static SegmentDetailEmbeddable toEmbeddable(SegmentDetail domain) {
    return SegmentDetailEmbeddable.of(domain.getCode(), domain.getLibelle());
  }
}
