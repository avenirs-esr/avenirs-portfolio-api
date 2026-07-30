package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.data;

import java.util.UUID;

public record StaffCreationData(
    UUID userId, String bio, String institutionEmail, UUID institutionId, UUID groupId) {}
