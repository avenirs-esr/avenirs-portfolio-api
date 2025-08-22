package fr.avenirsesr.portfolio.shared.domain.port.output.repository;

import fr.avenirsesr.portfolio.shared.domain.model.DeletableAvenirsBaseModel;

public interface GenericDeletableRepositoryPort<D extends DeletableAvenirsBaseModel>
    extends GenericRepositoryPort<D> {
  void delete(D domain);
}
