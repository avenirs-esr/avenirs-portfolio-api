package fr.avenirsesr.portfolio.api.domain.model.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ESecurityKeysTest {

  @Test
  void shouldReturnSecretForValidKey() {
    String secret = ESecurityKeys.getSecretByKey("v1");
    assertThat(secret).isEqualTo("super-secret-v1");

    secret = ESecurityKeys.getSecretByKey("v2");
    assertThat(secret).isEqualTo("super-secret-v2");
  }

  @Test
  void shouldThrowExceptionForInvalidKey() {
    assertThatThrownBy(() -> ESecurityKeys.getSecretByKey("invalid-key"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid key: invalid-key");
  }

  @Test
  void shouldHaveCorrectNumberOfValues() {
    assertThat(ESecurityKeys.values()).hasSize(2);
    assertThat(ESecurityKeys.valueOf("V1")).isEqualTo(ESecurityKeys.V1);
    assertThat(ESecurityKeys.valueOf("V2")).isEqualTo(ESecurityKeys.V2);
  }
}
