package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TrackRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TrackMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import org.springframework.stereotype.Component;

@Component
public class TrackDatabaseRepository extends GenericJpaRepositoryAdapter<Track, TrackEntity>
    implements TrackRepository {
  public TrackDatabaseRepository(TrackJpaRepository jpaRepository) {
    super(jpaRepository, TrackMapper::fromDomain, TrackMapper::toDomain);
  }
}
