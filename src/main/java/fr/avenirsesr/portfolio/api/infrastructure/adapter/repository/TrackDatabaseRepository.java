package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TrackRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackDatabaseRepository implements TrackRepository {
  private final TrackJpaRepository jpaRepository;

  @Override
  public void save(Track track) {
    var entity = toEntity(track);
    jpaRepository.save(entity);
  }

  @Override
  public void saveAll(List<Track> tracks) {
    var entities = tracks.stream().map(TrackDatabaseRepository::toEntity).toList();
    jpaRepository.saveAll(entities);
  }

  public static TrackEntity toEntity(Track track) {
    return new TrackEntity(
        track.getId(),
        UserDatabaseRepository.toEntity(track.getUser()),
        track.getSkillLevels().stream().map(SkillLevelDatabaseRepository::toEntity).toList(),
        track.getAmses().stream().map(AMSDatabaseRepository::toEntity).toList());
  }
}
