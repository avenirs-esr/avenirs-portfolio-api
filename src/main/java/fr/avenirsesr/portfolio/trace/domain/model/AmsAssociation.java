package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import java.util.UUID;

public record AmsAssociation(UUID id, String title, EAmsStatus status) {}
