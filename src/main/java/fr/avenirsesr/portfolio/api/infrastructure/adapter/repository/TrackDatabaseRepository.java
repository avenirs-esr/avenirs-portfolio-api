package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TrackRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TrackMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TrackEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.TrackSpecification;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class TrackDatabaseRepository extends GenericJpaRepositoryAdapter<Track, TrackEntity>
    implements TrackRepository {
  public TrackDatabaseRepository(TrackJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        TrackMapper::fromDomain,
        trackEntity -> TrackMapper.toDomain(trackEntity));
  }

  @Override
  public List<Track> findLastsOf(User user, int limit) {
    return jpaSpecificationExecutor
        .findAll(
            TrackSpecification.ofUser(UserMapper.fromDomain(user)),
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
        .getContent()
        .stream()
        .map(trackEntity -> TrackMapper.toDomain(trackEntity))
        .toList();
  }
}
