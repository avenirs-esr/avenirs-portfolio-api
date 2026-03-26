package fr.avenirsesr.portfolio.trace.domain.data;

import fr.avenirsesr.portfolio.activity.domain.model.enums.EActivityThematic;
import java.util.UUID;

public record DeclaredActivityAssociationSearchInfoData(
    UUID id, String title, EActivityThematic thematic, boolean disabled) {}
