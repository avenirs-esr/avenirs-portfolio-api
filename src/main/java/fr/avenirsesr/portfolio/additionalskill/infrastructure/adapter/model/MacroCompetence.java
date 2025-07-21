package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

public record MacroCompetence(
    String type,
    String code,
    String libelle,
    String codeOgr,
    String riasecMajeur,
    String codeArborescence,
    String sousCategorie,
    String maturite,
    Objectif objectif) {}
