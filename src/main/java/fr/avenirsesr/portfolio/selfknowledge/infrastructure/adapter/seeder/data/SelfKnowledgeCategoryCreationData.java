package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;
import java.util.UUID;

public record SelfKnowledgeCategoryCreationData(
    UUID id,
    String title,
    String description,
    ESelfKnowledgeCategoryType type,
    boolean isMandatory) {}
