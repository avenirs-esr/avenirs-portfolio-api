package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification.TraceSpecification;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
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
  public PagedResult<Trace> findAllUnassociated(User user, PageCriteria pageCriteria) {
    var content =
        jpaSpecificationExecutor
            .findAll(
                TraceSpecification.ofUser(UserMapper.fromDomain(user))
                    .and(TraceSpecification.unassociated()),
                PageRequest.of(
                    pageCriteria.page(),
                    pageCriteria.pageSize(),
                    Sort.by(Sort.Direction.DESC, "updatedAt")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt"))))
            .getContent()
            .stream()
            .map(TraceMapper::toDomain)
            .toList();

    return new PagedResult<>(
        content, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), content.size()));
  }

  public void saveAllEntities(List<TraceEntity> entities) {
    super.saveAllEntities(entities);
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
