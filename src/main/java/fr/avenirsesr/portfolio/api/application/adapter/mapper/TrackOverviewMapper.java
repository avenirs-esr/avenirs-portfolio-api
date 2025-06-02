package fr.avenirsesr.portfolio.api.application.adapter.mapper;

import fr.avenirsesr.portfolio.api.application.adapter.dto.TrackOverviewDTO;
import fr.avenirsesr.portfolio.api.domain.model.Track;

public interface TrackOverviewMapper {
  static TrackOverviewDTO toDTO(Track track, String programName) {
    return new TrackOverviewDTO(
        track.getId(),
        track.getTitle(),
        track.getSkillLevels().size(),
        track.getAmses().size(),
        programName,
        track.isGroup(),
        track.getCreatedAt());
  }
}
