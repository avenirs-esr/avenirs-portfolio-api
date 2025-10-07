package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Rome4Version;
import java.util.Optional;

public interface Rome4VersionRepository extends GenericRepositoryPort<Rome4Version> {
  Optional<Rome4Version> findFirstByOrderByVersionDesc();
}
