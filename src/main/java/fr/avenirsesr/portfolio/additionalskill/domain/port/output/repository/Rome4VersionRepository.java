package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.Optional;

public interface Rome4VersionRepository extends GenericRepositoryPort<Rome4Version> {
  Optional<Rome4Version> findFirstByOrderByVersionDesc();
}
