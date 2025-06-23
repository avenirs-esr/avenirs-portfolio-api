package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.PagedResult;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.AMSSpecification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
  public PagedResult<AMS> findByUserIdViaCohorts(UUID userId, int page, int size) {
    Page<AMSEntity> pageResult =
        jpaSpecificationExecutor.findAll(
            AMSSpecification.belongsToUserViaCohorts(userId),
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate")));

    List<AMS> content = pageResult.getContent().stream().map(AMSMapper::toDomain).toList();

    return new PagedResult<>(
        content, pageResult.getTotalElements(), pageResult.getTotalPages(), page, size);
  }

  public void saveAllEntities(List<AMSEntity> entities) {
    if (entities != null) {
      jpaRepository.saveAll(entities);
    }
  }
}
