package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data;

import java.util.UUID;

public record SelfKnowledgeElementCreationData(
    UUID studentId, String title, String description, Integer rating, UUID categoryId) {}
