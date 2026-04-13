package fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;

public record AssociationCreationData(String id1, String id2, EAssociationType associationType) {}
