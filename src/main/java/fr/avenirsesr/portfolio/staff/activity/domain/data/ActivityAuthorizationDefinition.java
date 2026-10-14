package fr.avenirsesr.portfolio.staff.activity.domain.data;

import fr.avenirsesr.portfolio.staff.activity.domain.model.enums.EActivityRight;
import java.util.Set;
import java.util.UUID;

public record ActivityAuthorizationDefinition(UUID staffId, Set<EActivityRight> rights) {}
