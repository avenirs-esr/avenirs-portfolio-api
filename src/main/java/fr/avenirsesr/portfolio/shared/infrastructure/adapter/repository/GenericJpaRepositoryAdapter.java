package fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public abstract class GenericJpaRepositoryAdapter<D, E> implements GenericRepositoryPort<D> {
  protected final JpaRepository<E, UUID> jpaRepository;
  protected final JpaSpecificationExecutor<E> jpaSpecificationExecutor;
  private final Function<D, E> fromDomain;
  private final Function<E, D> toDomain;

  protected GenericJpaRepositoryAdapter(
      JpaRepository<E, UUID> jpaRepository,
      JpaSpecificationExecutor<E> jpaSpecificationExecutor,
      Function<D, E> fromDomain,
      Function<E, D> toDomain) {
    this.jpaRepository = jpaRepository;
    this.jpaSpecificationExecutor = jpaSpecificationExecutor;
    this.fromDomain = fromDomain;
    this.toDomain = toDomain;
  }

  @Override
  public D save(D domain) {
    return toDomain.apply(jpaRepository.save(fromDomain.apply(domain)));
  }

  @Override
  public List<D> saveAll(List<D> domains) {
    return jpaRepository.saveAll(domains.stream().map(fromDomain).toList()).stream()
        .map(toDomain)
        .toList();
  }

  public void saveAllEntities(List<E> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  @Override
  public Optional<D> findById(UUID id) {
    E entity = jpaRepository.findById(id).orElse(null);
    return entity == null ? Optional.empty() : Optional.of(toDomain.apply(entity));
  }

  @Override
  public void flush() {
    jpaRepository.flush();
  }

  @Override
  public void delete(D domain) {
    jpaRepository.delete(fromDomain.apply(domain));
  }
}
