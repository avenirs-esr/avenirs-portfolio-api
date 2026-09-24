package fr.avenirsesr.portfolio.file.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

class FileControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/files";
  private static final String STORAGE_PATH = "/storage";
  private static final String FILE_NAME = "cgu.html";
  private static final String FILE_CONTENT = "<html><body>Conditions générales</body></html>";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  private BodyInserters.MultipartInserter uploadBody(String mimeType, boolean isRestricted) {
    var builder = new MultipartBodyBuilder();
    builder
        .part("file", new ByteArrayResource(FILE_CONTENT.getBytes(StandardCharsets.UTF_8)))
        .filename(FILE_NAME)
        .contentType(MediaType.parseMediaType(mimeType));
    builder.part("isRestricted", String.valueOf(isRestricted));

    return BodyInserters.fromMultipartData(builder.build());
  }

  private String uploadAndGetId(boolean isRestricted) throws Exception {
    String body =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(uploadBody(MediaType.TEXT_HTML_VALUE, isRestricted))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return objectMapper.readTree(body).get("id").asText();
  }

  @Test
  void shouldUploadAnHtmlFileAndServeItsContentBack() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint and an html file");

    BddLogger.when("posting the file as the logged in student");
    BddLogger.then("201 CREATED is returned with the description of the stored file");

    String body =
        webTestClient
            .post()
            .uri(BASE_PATH)
            .header("X-Signed-Context", studentPayload)
            .header("X-Context-Signature", studentSignature)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(uploadBody(MediaType.TEXT_HTML_VALUE, false))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    JsonNode created = objectMapper.readTree(body);
    assertThat(created.get("id").asText()).isNotBlank();
    assertThat(created.get("fileName").asText()).isEqualTo(FILE_NAME);
    assertThat(created.get("fileType").asText()).isEqualTo(EFileType.HTML.name());
    assertThat(created.get("fileSize").asLong())
        .isEqualTo(FILE_CONTENT.getBytes(StandardCharsets.UTF_8).length);
    assertThat(created.get("url").asText())
        .isEqualTo(STORAGE_PATH + "/" + created.get("id").asText());

    BddLogger.and("the content is served back by the url it exposes");
    webTestClient
        .get()
        .uri(created.get("url").asText())
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class)
        .isEqualTo(FILE_CONTENT);
  }

  @Test
  void shouldNotServeTheContentOfARestrictedFile() throws Exception {
    BddLogger.given("the " + BASE_PATH + " endpoint and an html file to keep restricted");

    BddLogger.when("posting the file with isRestricted");
    String fileId = uploadAndGetId(true);

    BddLogger.then("its content is not served by the storage endpoint");
    webTestClient.get().uri(STORAGE_PATH + "/" + fileId).exchange().expectStatus().isNotFound();
  }

  @Test
  void shouldRejectAFileWhoseTypeIsNotSupported() {
    BddLogger.given("the " + BASE_PATH + " endpoint and a file of an unsupported type");

    BddLogger.when("posting the file as the logged in student");
    BddLogger.then("400 BAD REQUEST is returned");

    webTestClient
        .post()
        .uri(BASE_PATH)
        .header("X-Signed-Context", studentPayload)
        .header("X-Context-Signature", studentSignature)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(uploadBody("application/x-not-supported", false))
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldRejectAnUploadWithoutASignedContext() {
    BddLogger.given("the " + BASE_PATH + " endpoint and an html file");

    BddLogger.when("posting the file without the signed context headers");
    BddLogger.then("401 UNAUTHORIZED is returned");

    webTestClient
        .post()
        .uri(BASE_PATH)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(uploadBody(MediaType.TEXT_HTML_VALUE, false))
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }
}
