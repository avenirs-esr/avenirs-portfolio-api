package fr.avenirsesr.portfolio.api.infrastructure.adapter.security;

import java.util.Collections;
import java.util.UUID;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class HmacAuthenticationToken extends AbstractAuthenticationToken {

  private final UUID userId;

  public HmacAuthenticationToken(UUID userId) {
    super(Collections.emptyList());
    this.userId = userId;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return "";
  }

  @Override
  public Object getPrincipal() {
    return userId;
  }
}
