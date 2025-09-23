package fr.avenirsesr.portfolio.ams.application.adapter.mapper;

import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsProgressDTO;
import fr.avenirsesr.portfolio.ams.application.adapter.dto.AmsViewDTO;
import fr.avenirsesr.portfolio.ams.domain.dto.AmsView;
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

  static AmsViewDTO toDto(AmsView amsDto) {

    return new AmsViewDTO(
        amsDto.ams().getId(),
        amsDto.ams().getTitle(),
        amsDto.skillLevelCount(),
        amsDto.traceCount(),
        amsDto.ams().getStatus(),
        createAmsProgressMock(amsDto.ams().getStatus()));
  }
}
