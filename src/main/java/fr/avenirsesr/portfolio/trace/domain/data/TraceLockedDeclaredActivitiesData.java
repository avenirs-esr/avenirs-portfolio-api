package fr.avenirsesr.portfolio.trace.domain.data;

import java.util.List;
import java.util.UUID;

public record TraceLockedDeclaredActivitiesData(
    UUID traceId, String traceTitle, List<TraceDeclaredActivityData> lockedDeclaredActivities) {}
