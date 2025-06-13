package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.TraceSpecification;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class TraceDatabaseRepository extends GenericJpaRepositoryAdapter<Trace, TraceEntity>
    implements TraceRepository {
  public TraceDatabaseRepository(TraceJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        TraceMapper::fromDomain,
        traceEntity -> TraceMapper.toDomain(traceEntity));
  }

  @Override
  public List<Trace> findLastsOf(User user, int limit) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofUser(UserMapper.fromDomain(user)),
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
        .getContent()
        .stream()
        .map(traceEntity -> TraceMapper.toDomain(traceEntity))
        .toList();
  }

  public void saveAllEntities(List<TraceEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }
}
