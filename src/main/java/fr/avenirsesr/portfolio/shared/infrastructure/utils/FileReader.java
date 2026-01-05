package fr.avenirsesr.portfolio.shared.infrastructure.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileReader {
  private final ObjectMapper objectMapper;

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

  public <T> List<T> readJSON(String path, TypeReference<List<T>> typeRef) {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
      if (is == null) {
        throw new IllegalStateException("File not found: " + path);
      }
      return objectMapper.readValue(is, typeRef);
    } catch (Exception e) {
      throw new RuntimeException("Error while reading " + path, e);
    }
  }
}
