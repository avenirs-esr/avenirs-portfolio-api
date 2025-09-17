package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericDeletableJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.trace.domain.model.ETraceStatus;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification.TraceSpecification;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Where(clause = "deleted_at IS NULL")
public class TraceDatabaseRepository
    extends GenericDeletableJpaRepositoryAdapter<Trace, TraceEntity> implements TraceRepository {
  public TraceDatabaseRepository(TraceJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, TraceMapper::fromDomain, TraceMapper::toDomain);
  }

  @Override
  public List<Trace> findLastsOf(User user, int limit) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofUser(UserMapper.fromDomain(user))
                .and(TraceSpecification.notDeleted()),
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
        .getContent()
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }

  @Override
  public PagedResult<Trace> findAll(User user, PageCriteria pageCriteria, ETraceStatus status) {
    Specification<TraceEntity> specification =
        TraceSpecification.ofUser(UserMapper.fromDomain(user)).and(TraceSpecification.notDeleted());

    switch (status) {
      case UNASSOCIATED -> specification = specification.and(TraceSpecification.unassociated());
      case ASSOCIATED -> specification = specification.and(TraceSpecification.associated());
      case null -> {}
    }

    var results =
        jpaSpecificationExecutor.findAll(
            specification,
            PageRequest.of(
                pageCriteria.page(),
                pageCriteria.pageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"))));

    var content = results.getContent().stream().map(TraceMapper::toDomain).toList();

    return new PagedResult<>(
        content,
        new PageInfo(
            results.getPageable().getPageNumber(),
            results.getPageable().getPageSize(),
            results.getTotalElements()));
  }

  public void saveAllEntities(List<TraceEntity> entities) {
    super.saveAllEntities(entities);
  }

  @Override
  public List<Trace> findAllUnassociated(User user) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofUser(UserMapper.fromDomain(user))
                .and(TraceSpecification.notDeleted())
                .and(TraceSpecification.unassociated()))
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }
}
