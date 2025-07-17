package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CompetenceComplementaireDetaillee {
  private String type;
  private boolean obsolete;
  private String code;
  private String libelle;
  private String codeOgr;
  private boolean transitionNumerique;
  private EscoCompetence competenceEsco;
  private String riasecMajeur;
  private MacroCompetence macroCompetence;
}
