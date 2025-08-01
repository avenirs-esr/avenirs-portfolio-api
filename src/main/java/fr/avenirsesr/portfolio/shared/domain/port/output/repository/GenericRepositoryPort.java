package fr.avenirsesr.portfolio.shared.domain.port.output.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenericRepositoryPort<D> {
  Optional<D> findById(UUID id);

  D save(D domain);

  List<D> saveAll(List<D> collection);

  void flush();

  void delete(D domain);
}
