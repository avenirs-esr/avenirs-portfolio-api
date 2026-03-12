package fr.avenirsesr.portfolio.association.domain.port.input;

import fr.avenirsesr.portfolio.association.domain.data.ActivityTraceAssociationData;
import fr.avenirsesr.portfolio.association.domain.model.ActivityTraceAssociation;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.List;
import java.util.UUID;

public interface ActivityTraceAssociationService {
  List<ActivityTraceAssociation> createAll(List<ActivityTraceAssociationData> associationsData);

  List<ActivityTraceAssociation> getAllOf(DeclaredActivity declaredActivity);

  List<ActivityTraceAssociation> getAllOf(Trace trace);

  void deleteAllByIds(List<UUID> ids);
}
