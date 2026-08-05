package fr.avenirsesr.portfolio.student.trace.domain.data;

import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.util.UUID;

public record TraceAssociationData(UUID associationId, Trace trace) {}
