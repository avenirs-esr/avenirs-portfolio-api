package fr.avenirsesr.portfolio.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericDeletableJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.DateFilterSpecificationBuilder;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification.TraceFilterSpecificationBuilder;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.specification.TraceSpecification;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@SQLRestriction("deleted_at IS NULL")
public class TraceDatabaseRepository
    extends GenericDeletableJpaRepositoryAdapter<Trace, TraceEntity> implements TraceRepository {
  private final TraceJpaRepository jpaRepository;

  public TraceDatabaseRepository(TraceJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, TraceEntity.class, TraceMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Trace> findLastsOf(User user, int limit) {
    return findAll(
            TraceSpecification.ofUser(user).and(TraceSpecification.notDeleted()),
            PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
        .content();
  }

  @Override
  public Map<Trace, Boolean> isAssociated(List<Trace> traces) {
    if (traces == null || traces.isEmpty()) {
      return Map.of();
    }

    var associatedIds =
        jpaRepository
            .findAll(
                Specification.where(TraceSpecification.associated())
                    .and(
                        (root, query, cb) ->
                            root.get("id").in(traces.stream().map(Trace::getId).toList())))
            .stream()
            .map(TraceEntity::getId)
            .collect(Collectors.toSet());

    return traces.stream()
        .collect(
            Collectors.toMap(Function.identity(), trace -> associatedIds.contains(trace.getId())));
  }

  @Override
  public PagedResult<Trace> findAll(
      User user,
      String keyword,
      TraceFilter filter,
      DateFilter dateFilter,
      PageCriteria pageCriteria) {

    Specification<TraceEntity> specification =
        TraceSpecification.ofUser(user).and(TraceSpecification.notDeleted());

    var filterSpecification = new TraceFilterSpecificationBuilder().build(filter.toMap());
    if (filterSpecification.isPresent()) {
      specification = specification.and(filterSpecification.get());
    }

    if (dateFilter != null) {
      var dateFilterSpecification =
          new DateFilterSpecificationBuilder<TraceEntity>().build(dateFilter.toMap());
      if (dateFilterSpecification.isPresent())
        specification = specification.and(dateFilterSpecification.get());
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

    return findAll(
        specification, PageRequest.of(pageCriteria.page(), pageCriteria.pageSize(), sort));
  }

  @Override
  public List<Trace> findAll(User user, boolean isAssociated) {
    Specification<TraceEntity> specification =
        TraceSpecification.ofUser(user).and(TraceSpecification.notDeleted());

    specification =
        specification.and(
            isAssociated ? TraceSpecification.associated() : TraceSpecification.unassociated());

    return findAll(specification);
  }

  @Override
  public List<Trace> linkedWith(SkillLevelProgress skillLevelProgress) {
    return findAll(TraceSpecification.ofSkillLevelProgress(skillLevelProgress));
  }

  @Override
  public Map<SkillLevelProgress, List<Trace>> linkedWith(
      List<SkillLevelProgress> skillLevelProgresses) {
    return Map.of();
  }

  @Override
  public List<Trace> linkedWith(DeclaredSkillProgress declaredSkillProgress) {
    return findAll(TraceSpecification.ofDeclaredSkillProgress(declaredSkillProgress));
  }
}
