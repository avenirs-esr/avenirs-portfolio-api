package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import java.util.UUID;

public record ExternalUserCreationData(
    UUID userId,
    String firstName,
    String lastName,
    String email,
    EUserCategory category,
    String externalId,
    EExternalSource source) {}
