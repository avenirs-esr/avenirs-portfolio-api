package fr.avenirsesr.portfolio.shared.application.adapter.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestResourceUtils {
  public static String loadJson(String filename) throws IOException {
    try (InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("mock/" + filename)) {
      if (is == null) {
        throw new IllegalArgumentException("File not found: mock/" + filename);
      }
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
}
