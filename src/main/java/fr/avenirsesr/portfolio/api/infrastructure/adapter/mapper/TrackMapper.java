package fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import java.util.List;

public interface TrackMapper {
  static TrackEntity fromDomain(Track track) {
    return new TrackEntity(
        track.getId(),
        UserMapper.fromDomain(track.getUser()),
        track.getTitle(),
        track.getSkillLevels().stream()
            .map(
                skillLevel ->
                    SkillLevelMapper.fromDomain(
                        skillLevel,
                        SkillMapper.fromDomain(
                            skillLevel.getSkill(),
                            ProgramProgressMapper.fromDomain(
                                skillLevel.getSkill().getProgramProgress())),
                        List.of()))
            .toList(),
        track.getAmses().stream().map(AMSMapper::fromDomain).toList(),
        track.isGroup(),
        track.getCreatedAt());
  }

  static Track toDomain(TrackEntity trackEntity) {
    return Track.toDomain(
        trackEntity.getId(),
        UserMapper.toDomain(trackEntity.getUser()),
        trackEntity.getTitle(),
        trackEntity.getSkillLevels().stream()
            .map(
                skillLevelEntity ->
                    SkillLevelMapper.toDomain(
                        skillLevelEntity,
                        SkillMapper.toDomain(
                            skillLevelEntity.getSkill(),
                            ProgramProgressMapper.toDomain(
                                skillLevelEntity.getSkill().getProgramProgress()))))
            .toList(),
        trackEntity.getAmses().stream().map(AMSMapper::toDomain).toList(),
        trackEntity.isGroup(),
        trackEntity.getCreatedAt());
  }
}
