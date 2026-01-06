package fr.avenirsesr.portfolio.trace.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import java.util.List;
import java.util.UUID;

public record TraceCreationData(
    UUID userId,
    String title,
    boolean isGroup,
    ELanguage language,
    String aiJustification,
    String personalNote,
    List<TraceAttachementCreationData> attachements) {}
