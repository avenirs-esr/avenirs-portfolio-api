package fr.avenirsesr.portfolio.association.domain.data;

import java.util.UUID;

public record ActivityTraceAssociationData(UUID declaredActivityId, UUID traceId) {}
