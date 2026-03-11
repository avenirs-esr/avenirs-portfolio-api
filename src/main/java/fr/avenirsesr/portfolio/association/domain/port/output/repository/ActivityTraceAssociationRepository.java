package fr.avenirsesr.portfolio.association.domain.port.output.repository;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface ActivityTraceAssociationRepository
    extends GenericRepositoryPort<ActivityTraceAssociation> {
  List<ActivityTraceAssociation> findAllIn(List<ActivityTraceAssociationData> associations);
}
