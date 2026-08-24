package fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.domain.model.DateFilter;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.specification.DateFilterSpecificationBuilder;
import fr.avenirsesr.portfolio.common.language.infrastructure.adapter.utils.TranslationUtil;
import fr.avenirsesr.portfolio.student.trace.domain.filter.TraceFilter;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.student.trace.domain.port.output.repository.TraceRepository;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.mapper.TraceMapper;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.specification.TraceFilterSpecificationBuilder;
import fr.avenirsesr.portfolio.student.trace.infrastructure.adapter.specification.TraceSpecification;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.repository.GenericUserJpaRepositoryAdapter;
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
public class TraceDatabaseRepository extends GenericUserJpaRepositoryAdapter<Trace, TraceEntity>
    implements TraceRepository {
  private final TraceJpaRepository jpaRepository;

  public TraceDatabaseRepository(TraceJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, TraceEntity.class, TraceMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<Trace> findLastsOf(Student student, int limit) {
    return findAll(
            hasStudent(student),
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
      Student student,
      String keyword,
      TraceFilter filter,
      DateFilter dateFilter,
      PageCriteria pageCriteria,
      SortCriteria sortCriteria) {

    Specification<TraceEntity> specification = hasStudent(student);

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

    return findAll(
        specification,
        PageRequest.of(
            pageCriteria.page(),
            pageCriteria.pageSize(),
            TraceSpecification.toSort(sortCriteria, filter)));
  }

  @Override
  public List<Trace> findAll(Student student, boolean isAssociated) {
    Specification<TraceEntity> specification = hasStudent(student);

    specification =
        specification.and(
            isAssociated ? TraceSpecification.associated() : TraceSpecification.unassociated());

    return findAll(specification);
  }
}
