package fr.avenirsesr.portfolio.testutils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class BddLogger {

  private static void log(String prefix, String message) {
    System.out.println(prefix + " " + message);
  }

  public static void given(String message) {
    log("🔵 GIVEN", message);
  }

  public static void when(String message) {
    log("🔶 WHEN", message);
  }

  public static void then(String message) {
    log("🟩 THEN", message);
  }
}
