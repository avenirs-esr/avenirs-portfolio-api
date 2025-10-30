package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericDeletableJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.DateFilterSpecificationBuilder;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.student.progress.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification.TraceFilterSpecificationBuilder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification.TraceSpecification;
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
  private final TraceJpaRepository jpaRepository;

  public TraceDatabaseRepository(TraceJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, TraceMapper::fromDomain, TraceMapper::toDomain);
    this.jpaRepository = jpaRepository;
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
  public PagedResult<Trace> findAll(
      User user,
      String keyword,
      TraceFilter filter,
      DateFilter dateFilter,
      PageCriteria pageCriteria) {
    Specification<TraceEntity> filterSpecification =
        new TraceFilterSpecificationBuilder().build(filter.toMap());

    Specification<TraceEntity> specification =
        TraceSpecification.ofUser(UserMapper.fromDomain(user))
            .and(TraceSpecification.notDeleted())
            .and(filterSpecification);

    if (dateFilter != null) {
      Specification<TraceEntity> dateFilterSpecification =
          new DateFilterSpecificationBuilder<TraceEntity>().build(dateFilter.toMap());
      specification = specification.and(dateFilterSpecification);
    }

    if (keyword != null) {
      specification =
          specification.and(
              TraceSpecification.search(keyword, TranslationUtil.getRequestLanguage()));
    }

    Sort sort =
        filter.isAssociated() != null && !filter.isAssociated()
            ? Sort.by(Sort.Direction.ASC, "createdAt")
            : Sort.by(Sort.Direction.DESC, "updatedAt")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));

    var results =
        jpaSpecificationExecutor.findAll(
            specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));

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
  public List<Trace> findAll(User user, boolean isAssociated) {
    Specification<TraceEntity> specification =
        TraceSpecification.ofUser(UserMapper.fromDomain(user)).and(TraceSpecification.notDeleted());

    specification =
        specification.and(
            isAssociated ? TraceSpecification.associated() : TraceSpecification.unassociated());

    return jpaSpecificationExecutor.findAll(specification).stream()
        .map(TraceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Trace> linkedWith(AMS ams) {
    return jpaSpecificationExecutor
        .findAll(TraceSpecification.ofAms(AMSMapper.fromDomain(ams)))
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Trace> linkedWith(SkillLevelProgress skillLevelProgress) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofSkillLevelProgress(
                SkillLevelProgressMapper.fromDomain(skillLevelProgress)))
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Trace> linkedWith(AdditionalSkillProgress additionalSkillProgress) {
    return jpaSpecificationExecutor
        .findAll(
            TraceSpecification.ofAdditionalSkillProgress(
                AdditionalSkillProgressMapper.fromDomain(additionalSkillProgress)))
        .stream()
        .map(TraceMapper::toDomain)
        .toList();
  }
}
