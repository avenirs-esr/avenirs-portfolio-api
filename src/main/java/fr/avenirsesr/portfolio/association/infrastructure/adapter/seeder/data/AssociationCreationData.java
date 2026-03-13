package fr.avenirsesr.portfolio.association.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import java.util.UUID;

public record AssociationCreationData(UUID id1, UUID id2, EAssociationType associationType) {}
