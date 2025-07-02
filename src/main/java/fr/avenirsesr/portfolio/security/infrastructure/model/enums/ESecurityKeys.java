package fr.avenirsesr.portfolio.security.infrastructure.model.enums;

public enum ESecurityKeys {
  V1("v1", "super-secret-v1"),
  V2("v2", "super-secret-v2");

  private final String key;
  private final String secret;

  ESecurityKeys(String key, String secret) {
    this.key = key;
    this.secret = secret;
  }

  public static String getSecretByKey(String key) {
    for (ESecurityKeys securityKey : ESecurityKeys.values()) {
      if (securityKey.key.equals(key)) {
        return securityKey.secret;
      }
    }
    throw new IllegalArgumentException("Invalid key: " + key);
  }
}
