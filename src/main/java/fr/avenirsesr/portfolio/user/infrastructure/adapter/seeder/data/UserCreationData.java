package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import java.util.UUID;

public record UserCreationData(UUID id, String firstName, String lastName, String email) {}
