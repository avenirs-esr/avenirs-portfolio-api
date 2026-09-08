package fr.avenirsesr.portfolio.student.kit.application.adapter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.security.infrastructure.adapter.model.AvenirsSecurityHeaders;
import fr.avenirsesr.portfolio.common.testutils.BddLogger;
import fr.avenirsesr.portfolio.shared.infrastructure.ContainerConfigurationTest;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.SeederRunner;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.CreateTraceDTO;
import fr.avenirsesr.portfolio.student.trace.application.adapter.dto.UpdateTraceDTO;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

class KitControllerIT extends ContainerConfigurationTest {

  private static final String BASE_PATH = "/me/kit";
  private static final String DOWNLOAD_PATH = BASE_PATH + "/download-media";
  private static final String TRACE_BASE_PATH = "/me/traces";
  private static final String TRACE_ATTACHMENT_PATH = TRACE_BASE_PATH + "/{traceId}/attachment";

  @Autowired private WebTestClient webTestClient;
  @Autowired private ObjectMapper objectMapper;

  @Value("${user.student.payload}")
  private String studentPayload;

  @Value("${user.student.signature}")
  private String studentSignature;

  @Value("${user.second.student.payload}")
  private String secondStudentPayload;

  @Value("${user.second.student.signature}")
  private String secondStudentSignature;

  @Value("${user.no-permission.payload}")
  private String noPermissionPayload;

  @Value("${user.no-permission.signature}")
  private String noPermissionSignature;

  @BeforeAll
  void setup(@Autowired SeederRunner seederRunner) {
    seederRunner.run();
  }

  @Test
  void shouldIncludeValorizedTraceAttachmentInDownloadedKit() throws Exception {
    BddLogger.given("a valorized trace with an uploaded attachment");

    String title = "Trace kit nominale " + UUID.randomUUID();
    byte[] content = "contenu du fichier de test".getBytes();
    UUID traceId = createTrace(title, this::addStudentHeaders);
    String fileName = uploadAttachment(traceId, "rapport.pdf", content, this::addStudentHeaders);
    valorizeTrace(traceId, title, this::addStudentHeaders);

    BddLogger.when("downloading the kit");
    byte[] zipBytes = downloadKit(this::addStudentHeaders);

    BddLogger.then("the zip should contain the attachment under traces/<title>/<fileName>");
    Map<String, byte[]> entries = readZipEntries(zipBytes);
    String expectedEntry = "traces/" + title + "/" + fileName;

    assertThat(entries).containsKey(expectedEntry);
    assertThat(entries.get(expectedEntry)).isEqualTo(content);
  }

  @Test
  void shouldNotIncludeAnotherStudentsValorizedAttachment() throws Exception {
    BddLogger.given("a valorized trace with an attachment belonging to another student");

    String title = "Trace isolation " + UUID.randomUUID();
    byte[] content = "contenu prive de l'autre etudiant".getBytes();
    UUID traceId = createTrace(title, this::addSecondStudentHeaders);
    String fileName =
        uploadAttachment(traceId, "prive.pdf", content, this::addSecondStudentHeaders);
    valorizeTrace(traceId, title, this::addSecondStudentHeaders);

    BddLogger.when("downloading the kit as a different student");
    byte[] zipBytes = downloadKit(this::addStudentHeaders);

    BddLogger.then("the zip should not contain the other student's attachment");
    Map<String, byte[]> entries = readZipEntries(zipBytes);
    String otherStudentEntry = "traces/" + title + "/" + fileName;

    assertThat(entries).doesNotContainKey(otherStudentEntry);
    assertThat(entries.values()).noneMatch(bytes -> Arrays.equals(bytes, content));
  }

  @Test
  void shouldReturn403WhenDownloadingKitWithoutPermission() {
    BddLogger.given("an authenticated user without the kit:download-media:own permission");

    BddLogger.when("downloading the kit");
    webTestClient
        .get()
        .uri(DOWNLOAD_PATH)
        .headers(this::addNoPermissionHeaders)
        .exchange()
        .expectStatus()
        .isForbidden();

    BddLogger.then("it should return 403");
  }

  private UUID createTrace(String title, Consumer<HttpHeaders> headers) throws Exception {
    CreateTraceDTO dto =
        new CreateTraceDTO(title, ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null);

    String body =
        webTestClient
            .post()
            .uri(TRACE_BASE_PATH)
            .contentType(APPLICATION_JSON)
            .bodyValue(objectMapper.writeValueAsString(dto))
            .headers(headers)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return UUID.fromString(objectMapper.readTree(body).get("traceId").asText());
  }

  private String uploadAttachment(
      UUID traceId, String fileName, byte[] content, Consumer<HttpHeaders> headers)
      throws Exception {
    var builder = new MultipartBodyBuilder();
    builder
        .part(
            "file",
            new ByteArrayResource(content) {
              @Override
              public String getFilename() {
                return fileName;
              }
            })
        .contentType(MediaType.valueOf("application/pdf"));

    String body =
        webTestClient
            .post()
            .uri(TRACE_ATTACHMENT_PATH, traceId)
            .headers(headers)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(String.class)
            .returnResult()
            .getResponseBody();

    return objectMapper.readTree(body).get("fileName").asText();
  }

  private void valorizeTrace(UUID traceId, String title, Consumer<HttpHeaders> headers) {
    UpdateTraceDTO dto =
        new UpdateTraceDTO(
            title, ELanguage.FRENCH, ETraceAuthorType.PERSONAL, null, null, null, true);

    webTestClient
        .put()
        .uri(TRACE_BASE_PATH + "/{traceId}", traceId)
        .contentType(APPLICATION_JSON)
        .bodyValue(dto)
        .headers(headers)
        .exchange()
        .expectStatus()
        .isOk();
  }

  private byte[] downloadKit(Consumer<HttpHeaders> headers) {
    return webTestClient
        .get()
        .uri(DOWNLOAD_PATH)
        .headers(headers)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .expectBody(byte[].class)
        .returnResult()
        .getResponseBody();
  }

  private Map<String, byte[]> readZipEntries(byte[] zipBytes) throws IOException {
    Map<String, byte[]> entries = new LinkedHashMap<>();
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        entries.put(entry.getName(), zis.readAllBytes());
        zis.closeEntry();
      }
    }
    return entries;
  }

  private void addStudentHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, studentPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, studentSignature);
  }

  private void addSecondStudentHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, secondStudentPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, secondStudentSignature);
  }

  private void addNoPermissionHeaders(HttpHeaders headers) {
    headers.add(AvenirsSecurityHeaders.SIGNED_CONTEXT, noPermissionPayload);
    headers.add(AvenirsSecurityHeaders.CONTEXT_SIGNATURE, noPermissionSignature);
  }
}
