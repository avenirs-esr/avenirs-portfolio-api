package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

public record CompetenceComplementaireDetaillee(
    String type,
    boolean obsolete,
    String code,
    String libelle,
    String codeOgr,
    boolean transitionNumerique,
    EscoCompetence competenceEsco,
    String riasecMajeur,
    MacroCompetence macroCompetence) {}
