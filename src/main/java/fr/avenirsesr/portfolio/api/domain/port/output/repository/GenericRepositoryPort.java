package fr.avenirsesr.portfolio.api.domain.port.output.repository;

import java.util.List;

public interface GenericRepositoryPort<D> {
  void save(D domain);

  void saveAll(List<D> collection);
}
