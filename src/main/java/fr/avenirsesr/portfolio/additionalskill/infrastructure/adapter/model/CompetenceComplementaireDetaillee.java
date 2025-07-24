package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import java.util.UUID;

public record CompetenceComplementaireDetaillee(
    UUID id,
    String type,
    boolean obsolete,
    String code,
    String libelle,
    String codeOgr,
    boolean transitionNumerique,
    EscoCompetence competenceEsco,
    String riasecMajeur,
    MacroCompetence macroCompetence) {}
