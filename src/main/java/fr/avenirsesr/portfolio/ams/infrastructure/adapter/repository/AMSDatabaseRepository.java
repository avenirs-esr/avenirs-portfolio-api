package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification.AMSSpecification;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PageInfo;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelProgressMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AMSDatabaseRepository extends GenericJpaRepositoryAdapter<AMS, AMSEntity>
    implements AMSRepository {
  private final AMSJpaRepository jpaRepository;

  public AMSDatabaseRepository(AMSJpaRepository repository) {
    super(repository, repository, AMSMapper::fromDomain, AMSMapper::toDomain);
    this.jpaRepository = repository;
  }

  @Override
  public PagedResult<AMS> findByUserIdViaCohortsAndSkillLevelProgresses(
      UUID userId, List<SkillLevelProgress> skillLevelProgresses, PageCriteria pageCriteria) {
    Specification<AMSEntity> spec = AMSSpecification.belongsToUserViaCohorts(userId);

    if (!skillLevelProgresses.isEmpty()) {
      spec =
          spec.and(
              AMSSpecification.hasSkillLevelProgress(
                  skillLevelProgresses.stream()
                      .map(SkillLevelProgressMapper::fromDomain)
                      .toList()));
    }

    Page<AMSEntity> pageResult =
        jpaSpecificationExecutor.findAll(
            spec,
            PageRequest.of(
                pageCriteria.page(),
                pageCriteria.pageSize(),
                Sort.by(Sort.Direction.DESC, "startDate")));

    List<AMS> content = pageResult.getContent().stream().map(AMSMapper::toDomain).toList();

    return new PagedResult<>(
        content, new PageInfo(pageCriteria.page(), pageCriteria.pageSize(), content.size()));
  }

  public void saveAllEntities(List<AMSEntity> entities) {
    if (entities != null) {
      jpaRepository.saveAll(entities);
    }
  }
}
