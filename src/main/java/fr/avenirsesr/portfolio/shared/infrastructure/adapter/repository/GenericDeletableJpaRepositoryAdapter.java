package fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.domain.model.DeletableAvenirsBaseModel;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericDeletableRepositoryPort;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.DeletableAvenirsBaseEntity;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public abstract class GenericDeletableJpaRepositoryAdapter<
        D extends DeletableAvenirsBaseModel, E extends DeletableAvenirsBaseEntity>
    extends GenericJpaRepositoryAdapter<D, E> implements GenericDeletableRepositoryPort<D> {
  protected final JpaRepository<E, UUID> jpaRepository;
  protected final JpaSpecificationExecutor<E> jpaSpecificationExecutor;
  private final Function<D, E> fromDomain;
  private final Function<E, D> toDomain;

  protected GenericDeletableJpaRepositoryAdapter(
      JpaRepository<E, UUID> jpaRepository,
      JpaSpecificationExecutor<E> jpaSpecificationExecutor,
      Function<D, E> fromDomain,
      Function<E, D> toDomain) {
    super(jpaRepository, jpaSpecificationExecutor, fromDomain, toDomain);
    this.jpaRepository = jpaRepository;
    this.jpaSpecificationExecutor = jpaSpecificationExecutor;
    this.fromDomain = fromDomain;
    this.toDomain = toDomain;
  }

  @Override
  public void delete(D domain) {
    jpaRepository.delete(fromDomain.apply(domain));
  }
}
