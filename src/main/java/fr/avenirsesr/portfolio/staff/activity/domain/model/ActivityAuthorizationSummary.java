package fr.avenirsesr.portfolio.staff.activity.domain.model;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.Set;
import java.util.UUID;

public record ActivityAuthorizationSummary(
    UUID activityId, String activityTitle, Set<EActivityRight> rights) {}
