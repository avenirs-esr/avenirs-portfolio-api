package fr.avenirsesr.portfolio.ams.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.specification.AMSSpecification;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class AMSDatabaseRepository extends GenericJpaRepositoryAdapter<AMS, AMSEntity>
    implements AMSRepository {
  private final AMSJpaRepository jpaRepository;

  public AMSDatabaseRepository(AMSJpaRepository repository) {
    super(repository, repository, AMSMapper::fromDomain, AMSMapper::toDomain);
    this.jpaRepository = repository;
  }

  @Override
  public PagedResult<AMS> findByStudent(Student student, PageCriteria pageCriteria) {
    Page<AMSEntity> pageResult =
        jpaSpecificationExecutor.findAll(
            AMSSpecification.belongsTo(student),
            PageRequest.of(
                pageCriteria.page(),
                pageCriteria.pageSize(),
                Sort.by(Sort.Direction.DESC, "startDate")));

    List<AMS> content = pageResult.getContent().stream().map(AMSMapper::toDomain).toList();

    return new PagedResult<>(
        content,
        new PageInfo(
            pageResult.getPageable().getPageNumber(),
            pageResult.getPageable().getPageSize(),
            pageResult.getTotalElements()));
  }

  @Override
  public List<AMS> findAllByStudent(Student student) {
    return jpaSpecificationExecutor.findAll(AMSSpecification.belongsTo(student)).stream()
        .map(AMSMapper::toDomain)
        .toList();
  }

  public void saveAllEntities(List<AMSEntity> entities) {
    if (entities != null) {
      jpaRepository.saveAll(entities);
    }
  }
}
