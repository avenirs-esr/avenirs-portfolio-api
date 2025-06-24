package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.TraceSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class TraceDatabaseRepository extends GenericJpaRepositoryAdapter<Trace, TraceEntity>
    implements TraceRepository {
  public TraceDatabaseRepository(TraceJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, TraceMapper::fromDomain, TraceMapper::toDomain);
  }

  @Override
  public List<Trace> findLastsOf(User user, int limit) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofUser(UserMapper.fromDomain(user)),
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
        .getContent()
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Trace> findAllPage(User user, int page, int pageSize) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofUser(UserMapper.fromDomain(user)),
            PageRequest.of(
                page,
                pageSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"))))
        .getContent()
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }

  @Override
  public Page<TraceEntity> findAllUnassociatedPage(User user, int page, int pageSize) {
    return jpaSpecificationExecutor.findAll(
        TraceSpecification.ofUser(UserMapper.fromDomain(user))
            .and(TraceSpecification.unassociated()),
        PageRequest.of(
            page,
            pageSize,
            Sort.by(Sort.Direction.DESC, "updatedAt")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"))));
  }

  public void saveAllEntities(List<TraceEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  @Override
  public List<Trace> findAllUnassociated(User user) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofUser(UserMapper.fromDomain(user))
                .and(TraceSpecification.unassociated()))
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }
}
