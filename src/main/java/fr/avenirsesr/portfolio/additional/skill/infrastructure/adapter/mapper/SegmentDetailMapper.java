package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additional.skill.domain.model.SegmentDetail;

public interface SegmentDetailMapper {
  static SegmentDetail toDomain(String code, String libelle) {
    return SegmentDetail.create(code, libelle);
  }
}
