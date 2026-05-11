package fr.avenirsesr.portfolio.activity.domain.data;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import java.util.Optional;
import java.util.UUID;

public record ActivityPresentationData(
    Activity activity, Optional<UUID> subscribedDeclaredActivity) {}
