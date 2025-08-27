package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Competence {

  private String code;
  private String libelle;
  private MacroCompetence macroCompetence;

  @Getter
  @Setter
  public static class MacroCompetence {

    private String code;
    private String libelle;
    private Objectif objectif;

    @Getter
    @Setter
    public static class Objectif {

      private String code;
      private String libelle;
      private Enjeu enjeu;

      @Getter
      @Setter
      public static class Enjeu {

        private String code;
        private String libelle;
        private DomaineCompetence domaineCompetence;

        @Getter
        @Setter
        public static class DomaineCompetence {

          private String code;
          private String libelle;
        }
      }
    }
  }
}
