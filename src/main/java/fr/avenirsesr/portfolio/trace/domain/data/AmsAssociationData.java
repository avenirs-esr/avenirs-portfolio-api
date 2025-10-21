package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import java.util.UUID;

public record AmsAssociationData(UUID id, String title, EAmsStatus status) {}
