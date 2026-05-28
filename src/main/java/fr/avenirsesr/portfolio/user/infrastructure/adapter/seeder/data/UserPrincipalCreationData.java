package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import java.util.UUID;

public record UserPrincipalCreationData(UUID userId, String eppn) {}
