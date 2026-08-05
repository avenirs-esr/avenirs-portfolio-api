package fr.avenirsesr.portfolio.student.association.domain.data;

import java.util.UUID;

public record AssociationSearchResultData(
    UUID id, String title, String category, boolean disabled) {}
