package fr.avenirsesr.portfolio.association.domain.data;

import fr.avenirsesr.portfolio.association.domain.model.EAssociationType;
import java.util.UUID;

public record AssociationData(UUID id1, UUID id2, EAssociationType associationType) {}
