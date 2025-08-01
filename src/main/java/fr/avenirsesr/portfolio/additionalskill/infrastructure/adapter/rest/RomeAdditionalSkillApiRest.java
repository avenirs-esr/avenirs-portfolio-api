package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.rest;

import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.Competence;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RomeAdditionalSkillApiRest implements RomeAdditionalSkillApi {

  private static final String FIELDS_PARAMETER =
      "libelle,code,@competencedetaillee(riasecmineur,macrocompetence(libelle,transferable,@macrosavoiretreprofessionnel(qualiteprofessionnelle),souscategorie,code,riasecmineur,codearborescence,objectif(libelle,enjeu(libelle,code,codearborescence,domainecompetence(libelle,code,codearborescence)),code,codearborescence),codeogr,maturite,riasecmajeur),riasecmajeur)";

  public static final String SYMBOL_EQUAL = "=";

  private static final String FIELD_GRANT_TYPE = "grant_type";

  private static final String VALUE_GRANT_TYPE = "client_credentials";

  private static final String FIELD_SCOPE = "scope";

  private static final String VALUE_SCOPE = "api_rome-competencesv1 nomenclatureRome";

  private static final String FIELD_CLIENT_ID = "client_id";

  private static final String FIELD_CLIENT_SECRET = "client_secret";
  public static final String SYMBOL_AND = "&";

  @Value("${france.travail.base.url}")
  private String franceTravailBaseUrl;

  @Value("${rome.4.competence.base.url}")
  private String rome4CompetenceBaseUrl;

  @Value("${rome.4.competence.client.id}")
  private String rome4CompetenceClientId;

  @Value("${rome.4.competence.client.secret}")
  private String rome4CompetenceClientSecret;

  private WebClient rome4WebClient;

  private WebClient franceTravailWebClient;

  @PostConstruct
  public void init() {
    rome4WebClient = WebClient.builder().baseUrl(rome4CompetenceBaseUrl).build();
    franceTravailWebClient = WebClient.builder().baseUrl(franceTravailBaseUrl).build();
  }

  @Override
  public Rome4Version fetchRomeVersion() {
    TokenResponse tokenResponse = getAccessToken();
    return rome4WebClient
        .get()
        .uri("/version")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.accessToken())
        .retrieve()
        .bodyToMono(Rome4Version.class)
        .block();
  }

  @Override
  public List<Competence> fetchAdditionalSkills() {
    TokenResponse tokenResponse = getAccessToken();

    List<Competence> competenceList =
        rome4WebClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder.path("/competence").queryParam("champs", FIELDS_PARAMETER).build())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.accessToken())
            .retrieve()
            .bodyToFlux(Competence.class)
            .filter(competence -> Objects.nonNull(competence.getMacroCompetence()))
            .collectList()
            .block();

    if (competenceList == null) competenceList = List.of();

    return competenceList;
  }

  private TokenResponse getAccessToken() {

    String body =
        buildParameter(FIELD_GRANT_TYPE, VALUE_GRANT_TYPE)
            + SYMBOL_AND
            + buildParameter(FIELD_CLIENT_ID, rome4CompetenceClientId)
            + SYMBOL_AND
            + buildParameter(FIELD_CLIENT_SECRET, rome4CompetenceClientSecret)
            + SYMBOL_AND
            + buildParameter(FIELD_SCOPE, VALUE_SCOPE);

    return franceTravailWebClient
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/connexion/oauth2/access_token")
                    .queryParam("realm", "/partenaire")
                    .build())
        .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(TokenResponse.class)
        .block();
  }

  private String buildParameter(String field, String value) {
    return field + SYMBOL_EQUAL + value;
  }
}
