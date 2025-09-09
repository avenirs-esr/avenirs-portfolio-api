package fr.avenirsesr.portfolio.testutils;

public final class BddLogger {

  private static void log(String prefix, String message) {
    System.out.println(prefix + " " + message);
  }

  private BddLogger() {}

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
