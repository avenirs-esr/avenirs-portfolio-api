package fr.avenirsesr.portfolio.shared.infrastructure.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface FileReader {
  static <T> List<T> readCSV(String fileName, String separator, Function<String[], T> mapper) {
    try (var inputStream = FileReader.class.getResourceAsStream(fileName);
        var reader =
            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

      return reader
          .lines()
          .skip(1)
          .map(line -> line.split(separator))
          .map(mapper)
          .collect(Collectors.toList());

    } catch (IOException | NullPointerException e) {
      throw new RuntimeException("Erreur de lecture du fichier CSV", e);
    }
  }
}
