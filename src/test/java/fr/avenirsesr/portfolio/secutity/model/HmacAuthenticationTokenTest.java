package fr.avenirsesr.portfolio.secutity.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.avenirsesr.portfolio.security.infrastructure.model.HmacAuthenticationToken;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class HmacAuthenticationTokenTest {

  @Test
  void shouldCreateTokenWithUserId() {
    UUID userId = UUID.randomUUID();
    HmacAuthenticationToken token = new HmacAuthenticationToken(userId);

    assertThat(token.getPrincipal()).isEqualTo(userId);
    assertThat(token.getCredentials()).isEqualTo("");
    assertThat(token.isAuthenticated()).isTrue();
  }

  @Test
  void shouldHaveNoAuthorities() {
    UUID userId = UUID.randomUUID();
    HmacAuthenticationToken token = new HmacAuthenticationToken(userId);

    assertThat(token.getAuthorities()).isEmpty();
  }

  @Test
  void shouldImplementAuthenticationInterface() {
    UUID userId = UUID.randomUUID();
    HmacAuthenticationToken token = new HmacAuthenticationToken(userId);

    assertThat(token).isInstanceOf(Authentication.class);
  }
}
