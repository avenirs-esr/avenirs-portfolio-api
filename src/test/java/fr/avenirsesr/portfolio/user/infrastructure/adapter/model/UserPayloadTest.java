package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.avenirsesr.portfolio.testutils.BddLogger;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserPayloadTest {

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void shouldCreateUserPayload() {
    BddLogger.given("user payload properties");
    UUID sub = UUID.randomUUID();
    Instant iat = Instant.now();
    Instant exp = iat.plusSeconds(3600);

    BddLogger.when("creating an UserPayload");
    UserPayload userPayload = new UserPayload();
    userPayload.setSub(sub);
    userPayload.setIat(iat);
    userPayload.setExp(exp);

    BddLogger.then("it should create the UserPayload");
    assertThat(userPayload.getSub()).isEqualTo(sub);
    assertThat(userPayload.getIat()).isEqualTo(iat);
    assertThat(userPayload.getExp()).isEqualTo(exp);
  }

  @Test
  void shouldSerializeAndDeserializeUserPayload() throws Exception {
    BddLogger.given("an UserPayload");
    UUID sub = UUID.randomUUID();
    Instant iat = Instant.now();
    Instant exp = iat.plusSeconds(3600);

    UserPayload userPayload = new UserPayload();
    userPayload.setSub(sub);
    userPayload.setIat(iat);
    userPayload.setExp(exp);

    String json = objectMapper.writeValueAsString(userPayload);

    BddLogger.when("deserializing the UserPayload");
    UserPayload deserializedUserPayload = objectMapper.readValue(json, UserPayload.class);

    BddLogger.then("it should allow access to user payload properties");
    assertThat(deserializedUserPayload.getSub()).isEqualTo(sub);
    assertThat(deserializedUserPayload.getIat()).isEqualTo(iat);
    assertThat(deserializedUserPayload.getExp()).isEqualTo(exp);
  }
}
