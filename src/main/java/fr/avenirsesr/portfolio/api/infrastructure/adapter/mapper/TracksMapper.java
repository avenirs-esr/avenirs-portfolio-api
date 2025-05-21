package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;

public interface TracksMapper {
  static TrackEntity fromModelToEntity(Track track) {
    return new TrackEntity();
  }

  static Track fromEntityToModel(TrackEntity trackEntity) {
    return Track.toDomain(
        trackEntity.getId(),
        UserMapper.fromEntityToModel(trackEntity.getUser()),
        trackEntity.getSkillLevels().stream().map(SkillLevelMapper::fromEntityToModel).toList(),
        trackEntity.getAmses().stream().map(AMSMapper::fromEntityToModel).toList());
  }
}
