package fr.avenirsesr.portfolio.file.domain.port.output.repository;

import fr.avenirsesr.portfolio.file.domain.model.TraceAttachment;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.List;

public interface TraceAttachmentRepository extends GenericRepositoryPort<TraceAttachment> {
  List<TraceAttachment> findByTrace(Trace trace);
}
