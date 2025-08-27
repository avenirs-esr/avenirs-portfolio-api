package fr.avenirsesr.portfolio.security.infrastructure.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils.UuidV7Generator;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class HmacAuthenticationTokenTest {

  private final UuidGenerator uuidGenerator = new UuidV7Generator();

  @Test
  void shouldCreateTokenWithUserId() {
    UUID userId = uuidGenerator.generate();
    HmacAuthenticationToken token = new HmacAuthenticationToken(userId);

    assertThat(token.getPrincipal()).isEqualTo(userId);
    assertThat(token.getCredentials()).isEqualTo("");
    assertThat(token.isAuthenticated()).isTrue();
  }

  @Test
  void shouldHaveNoAuthorities() {
    UUID userId = uuidGenerator.generate();
    HmacAuthenticationToken token = new HmacAuthenticationToken(userId);

    assertThat(token.getAuthorities()).isEmpty();
  }

  @Test
  void shouldImplementAuthenticationInterface() {
    UUID userId = uuidGenerator.generate();
    HmacAuthenticationToken token = new HmacAuthenticationToken(userId);

    assertThat(token).isInstanceOf(Authentication.class);
  }
}
