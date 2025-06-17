package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.AmsProgress;
import fr.avenirsesr.portfolio.api.domain.model.enums.EAmsStatus;
import java.util.List;

public interface AmsViewMapper {
  static AmsViewDTO toDto(AMS ams) {

    return new AmsViewDTO(
        ams.getId(),
        ams.getTitle(),
        ams.getSkillLevels().size(),
        ams.getTraces().size(),
        ams.getStatus(),
        // TODO: calculate progress
        new AmsProgress(0, 0, 0));
  }
}
