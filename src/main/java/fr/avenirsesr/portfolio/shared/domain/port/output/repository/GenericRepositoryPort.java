package fr.avenirsesr.portfolio.shared.domain.port.output.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenericRepositoryPort<D> {
  Optional<D> findById(UUID id);

  void save(D domain);

  void saveAll(List<D> collection);

  void flush();

  void deleteById(UUID id);
}
