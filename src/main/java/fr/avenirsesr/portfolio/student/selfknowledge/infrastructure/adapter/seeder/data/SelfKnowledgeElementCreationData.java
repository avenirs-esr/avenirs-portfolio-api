package fr.avenirsesr.portfolio.student.selfknowledge.infrastructure.adapter.seeder.data;

import java.util.UUID;

public record SelfKnowledgeElementCreationData(
    UUID studentId, String title, String description, Integer rating, String category) {}
