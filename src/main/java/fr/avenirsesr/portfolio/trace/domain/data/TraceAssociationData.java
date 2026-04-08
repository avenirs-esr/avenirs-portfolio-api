package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.util.UUID;

public record TraceAssociationData(UUID associationId, Trace trace) {}
