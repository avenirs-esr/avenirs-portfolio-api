package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class GenericJpaRepositoryAdapter<D, E> implements GenericRepositoryPort<D> {
  private final JpaRepository<E, UUID> jpaRepository;
  private final Function<D, E> fromDomain;
  private final Function<E, D> toDomain;

  protected GenericJpaRepositoryAdapter(
      JpaRepository<E, UUID> jpaRepository, Function<D, E> fromDomain, Function<E, D> toDomain) {
    this.jpaRepository = jpaRepository;
    this.fromDomain = fromDomain;
    this.toDomain = toDomain;
  }

  @Override
  public void save(D domain) {
    jpaRepository.save(fromDomain.apply(domain));
  }

  @Override
  public void saveAll(List<D> domains) {
    jpaRepository.saveAll(domains.stream().map(fromDomain).toList());
  }

  @Override
  public Optional<D> findById(UUID id) {
    return Optional.ofNullable(toDomain.apply(jpaRepository.findById(id).orElse(null)));
  }
}
