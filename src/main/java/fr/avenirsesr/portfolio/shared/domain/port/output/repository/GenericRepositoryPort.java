package fr.avenirsesr.portfolio.shared.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.model.AvenirsBaseModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GenericRepositoryPort<D extends AvenirsBaseModel> {
  Optional<D> findById(UUID id);

  void save(D domain);

  void saveAll(List<D> collection);

  void flush();
}
