package fr.avenirsesr.portfolio.ams.application.adapter.mapper;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsProgressDTO;
import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;

public interface AmsViewMapper {

  static AmsProgressDTO createAmsProgressMock(EAmsStatus status) {
    if (status == EAmsStatus.NOT_STARTED) {
      return AmsProgressDTO.createNotStartedMock();
    }
    if (status == EAmsStatus.IN_PROGRESS) {
      return AmsProgressDTO.createInProgressMock();
    }
    return AmsProgressDTO.createSubmittedOrCompletedMock();
  }

  static AmsViewDTO toDto(AMS ams) {

    return new AmsViewDTO(
        ams.getId(),
        ams.getTitle(),
        ams.getSkillLevels().size(),
        ams.getTraces().size(),
        ams.getStatus(),
        // TODO: calculate progress
        createAmsProgressMock(ams.getStatus()));
  }
}
