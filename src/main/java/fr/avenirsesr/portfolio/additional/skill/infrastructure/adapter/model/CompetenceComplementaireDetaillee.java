package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model;

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
