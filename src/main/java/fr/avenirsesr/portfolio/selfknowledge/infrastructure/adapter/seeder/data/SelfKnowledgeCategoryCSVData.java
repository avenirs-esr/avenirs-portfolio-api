package fr.avenirsesr.portfolio.selfknowledge.infrastructure.adapter.seeder.data;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.data.CsvReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelfKnowledgeCategoryCSVData {

  private static final String PATH_FRENCH = "seeder/self-knowledge-categories.fr.csv";
  private static final String PATH_ENGLISH = "seeder/self-knowledge-categories.en.csv";
  private static final String PATH_SPANISH = "seeder/self-knowledge-categories.es.csv";
  private static final String DELIMITER = ";";

  private static List<CsvSelfKnowledgeCategoryDto> read(String path) {
    return new CsvReader<CsvSelfKnowledgeCategoryDto>()
        .readCSV(
            DELIMITER,
            SelfKnowledgeCategoryCSVData.class.getClassLoader().getResourceAsStream(path),
            values ->
                new CsvSelfKnowledgeCategoryDto(
                    values[0], values[1], Boolean.parseBoolean(values[2])));
  }

  private static final Map<ELanguage, ArrayList<CsvSelfKnowledgeCategoryDto>> data =
      Map.of(
          ELanguage.FRENCH, new ArrayList<>(read(PATH_FRENCH)),
          ELanguage.ENGLISH, new ArrayList<>(read(PATH_ENGLISH)),
          ELanguage.SPANISH, new ArrayList<>(read(PATH_SPANISH)));

  public static List<CsvSelfKnowledgeCategoryDto> getAll(ELanguage language) {
    return List.copyOf(data.get(language));
  }

  public static Map<ELanguage, List<CsvSelfKnowledgeCategoryDto>> getAllByLanguage() {
    return Map.copyOf(data);
  }
}
