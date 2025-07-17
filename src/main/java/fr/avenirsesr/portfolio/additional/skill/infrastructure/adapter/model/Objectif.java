package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Objectif {
  private String code;
  private String libelle;
  private String codeArborescence;
  private Enjeu enjeu;
}
