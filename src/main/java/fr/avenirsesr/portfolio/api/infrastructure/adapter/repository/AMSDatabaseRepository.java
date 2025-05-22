package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.AMSRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.AMSMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.AMSEntity;
import org.springframework.stereotype.Component;

@Component
public class AMSDatabaseRepository extends GenericJpaRepositoryAdapter<AMS, AMSEntity>
    implements AMSRepository {

  public AMSDatabaseRepository(AMSJpaRepository repository) {
    super(repository, AMSMapper::fromDomain, AMSMapper::toDomain);
  }
}
