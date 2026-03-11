package fr.avenirsesr.portfolio.association.domain.port.input;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import java.util.List;

public interface ActivityTraceAssociationService {
  List<ActivityTraceAssociation> createAll(List<ActivityTraceAssociationData> associationsData);
}
