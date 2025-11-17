package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategoryType;

public record CsvSelfKnowledgeCategoryDto(
    String title, String description, boolean mandatory, ESelfKnowledgeCategoryType type) {}
