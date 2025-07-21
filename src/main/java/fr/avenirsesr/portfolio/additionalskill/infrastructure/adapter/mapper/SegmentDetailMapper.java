package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.additionalskill.domain.model.SegmentDetail;

public interface SegmentDetailMapper {
  static SegmentDetail toDomain(String code, String libelle) {
    return SegmentDetail.toDomain(code, libelle);
  }
}
