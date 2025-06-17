package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.AMSSpecification;
import java.util.List;
import java.util.UUID;
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
  public List<AMS> findByUserIdViaCohorts(UUID userId, int page, int size) {
    return jpaSpecificationExecutor
        .findAll(
            AMSSpecification.belongsToUserViaCohorts(userId),
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startDate")))
        .getContent()
        .stream()
        .map(AMSMapper::toDomain)
        .toList();
  }

  public void saveAllEntities(List<AMSEntity> entities) {
    if (entities != null) {
      jpaRepository.saveAll(entities);
    }
  }
}
