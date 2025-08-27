package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.UuidV7Generator;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserPayloadTest {

  private ObjectMapper objectMapper;

  private final UuidGenerator uuidGenerator = new UuidV7Generator();

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void shouldCreateUserPayload() {
    UUID sub = uuidGenerator.generate();
    Instant iat = Instant.now();
    Instant exp = iat.plusSeconds(3600);

    UserPayload userPayload = new UserPayload();
    userPayload.setSub(sub);
    userPayload.setIat(iat);
    userPayload.setExp(exp);

    assertThat(userPayload.getSub()).isEqualTo(sub);
    assertThat(userPayload.getIat()).isEqualTo(iat);
    assertThat(userPayload.getExp()).isEqualTo(exp);
  }

  @Test
  void shouldSerializeAndDeserializeUserPayload() throws Exception {
    UUID sub = uuidGenerator.generate();
    Instant iat = Instant.now();
    Instant exp = iat.plusSeconds(3600);

    UserPayload userPayload = new UserPayload();
    userPayload.setSub(sub);
    userPayload.setIat(iat);
    userPayload.setExp(exp);

    String json = objectMapper.writeValueAsString(userPayload);
    UserPayload deserializedUserPayload = objectMapper.readValue(json, UserPayload.class);

    assertThat(deserializedUserPayload.getSub()).isEqualTo(sub);
    assertThat(deserializedUserPayload.getIat()).isEqualTo(iat);
    assertThat(deserializedUserPayload.getExp()).isEqualTo(exp);
  }
}
