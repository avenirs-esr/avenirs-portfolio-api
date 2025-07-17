package fr.avenirsesr.portfolio.additional.skill.application.adapter.mapper;

import fr.avenirsesr.portfolio.additional.skill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additional.skill.domain.model.SegmentDetail;
import java.util.List;
import java.util.Optional;

public interface AdditionalSkillMapper {
  static AdditionalSkillDTO toAdditionalSkillDTO(AdditionalSkill additionalSkill) {
    return new AdditionalSkillDTO(
        additionalSkill.getId(),
        additionalSkill.getPathSegments().getSkill().getLibelle(),
        List.of(
            getLabel(additionalSkill.getPathSegments().getIssue()),
            getLabel(additionalSkill.getPathSegments().getTarget()),
            getLabel(additionalSkill.getPathSegments().getMacroSkill())),
        additionalSkill.getType());
  }

  private static String getLabel(SegmentDetail segment) {
    return Optional.ofNullable(segment).map(SegmentDetail::getLibelle).orElse("");
  }
}
