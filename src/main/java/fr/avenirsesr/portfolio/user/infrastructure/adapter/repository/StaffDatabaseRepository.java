package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import org.springframework.stereotype.Repository;

@Repository
public class StaffDatabaseRepository extends GenericJpaRepositoryAdapter<Staff, StaffEntity>
    implements StaffRepository {
  private final StaffJpaRepository jpaRepository;

  public StaffDatabaseRepository(StaffJpaRepository repository) {
    super(repository, repository, StaffEntity.class, StaffMapper.INSTANCE);
    this.jpaRepository = repository;
  }
}
