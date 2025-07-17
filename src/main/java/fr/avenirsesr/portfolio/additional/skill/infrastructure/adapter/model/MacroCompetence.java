package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MacroCompetence {
  private String type;
  private String code;
  private String libelle;
  private String codeOgr;
  private String riasecMajeur;
  private String codeArborescence;
  private String sousCategorie;
  private String maturite;
  private Objectif objectif;
}
