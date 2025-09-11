package fr.avenirsesr.portfolio.shared.infrastructure.adapter.seeder.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Function;

public class CsvReader<T> {
  public ArrayList<T> readCSV(
      String DELIMITER, InputStream inputStream, Function<String[], T> rowMapper) {
    String line;
    var results = new ArrayList<T>();

    try (var br = new BufferedReader(new java.io.InputStreamReader(inputStream))) {
      while ((line = br.readLine()) != null) {
        String[] values = line.split(DELIMITER);
        results.add(rowMapper.apply(values));
      }
      return results;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
