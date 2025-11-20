package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.CsvReader;
import java.util.List;

public class SelfKnowledgeElementCSVData {
  private static final String PATH_STRENGTHS = "seeder/self-knowledge-elements-strengths.csv";
  private static final String PATH_VALUES = "seeder/self-knowledge-elements-values.csv";
  private static final String PATH_ASPIRATIONS = "seeder/self-knowledge-elements-aspirations.csv";
  private static final String DELIMITER = ";";
  private static final List<CsvSelfKnowledgeElementDto> dataStrenghs =
      new CsvReader<CsvSelfKnowledgeElementDto>()
          .readCSV(
              DELIMITER,
              SelfKnowledgeElementCSVData.class
                  .getClassLoader()
                  .getResourceAsStream(PATH_STRENGTHS),
              values ->
                  new CsvSelfKnowledgeElementDto(
                      values[0], values[1], parseIntegerOrNull(values[2])));

  private static final List<CsvSelfKnowledgeElementDto> dataValues =
      new CsvReader<CsvSelfKnowledgeElementDto>()
          .readCSV(
              DELIMITER,
              SelfKnowledgeElementCSVData.class.getClassLoader().getResourceAsStream(PATH_VALUES),
              values ->
                  new CsvSelfKnowledgeElementDto(
                      values[0], values[1], parseIntegerOrNull(values[2])));

  private static final List<CsvSelfKnowledgeElementDto> dataAspirations =
      new CsvReader<CsvSelfKnowledgeElementDto>()
          .readCSV(
              DELIMITER,
              SelfKnowledgeElementCSVData.class
                  .getClassLoader()
                  .getResourceAsStream(PATH_ASPIRATIONS),
              values ->
                  new CsvSelfKnowledgeElementDto(
                      values[0], values[1], parseIntegerOrNull(values[2])));

  public static List<CsvSelfKnowledgeElementDto> getDataStrenghs() {
    return List.copyOf(dataStrenghs);
  }

  public static List<CsvSelfKnowledgeElementDto> getDataValues() {
    return List.copyOf(dataValues);
  }

  public static List<CsvSelfKnowledgeElementDto> getDataAspirations() {
    return List.copyOf(dataAspirations);
  }

  private static Integer parseIntegerOrNull(String input) {
    if (input == null || input.isBlank() || input.equalsIgnoreCase("null")) {
      return null;
    }
    return Integer.valueOf(input);
  }
}
