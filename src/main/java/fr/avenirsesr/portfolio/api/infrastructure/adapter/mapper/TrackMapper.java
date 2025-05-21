package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;

public interface TrackMapper {
  static TrackEntity fromDomain(Track track) {
    return new TrackEntity(
        track.getId(),
        UserMapper.fromDomain(track.getUser()),
        track.getSkillLevels().stream().map(SkillLevelMapper::fromDomain).toList(),
        track.getAmses().stream().map(AMSMapper::fromDomain).toList());
  }

  static Track toDomain(TrackEntity trackEntity) {
    return Track.toDomain(
        trackEntity.getId(),
        UserMapper.toDomain(trackEntity.getUser()),
        trackEntity.getSkillLevels().stream().map(SkillLevelMapper::toDomain).toList(),
        trackEntity.getAmses().stream().map(AMSMapper::toDomain).toList());
  }
}
