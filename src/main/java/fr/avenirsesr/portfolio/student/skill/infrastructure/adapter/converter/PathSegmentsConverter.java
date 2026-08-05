package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Converter
public class PathSegmentsConverter implements AttributeConverter<List<String>, String> {

  private static final String SEPARATOR = " > ";

  @Override
  public String convertToDatabaseColumn(List<String> pathSegments) {
    if (pathSegments == null || pathSegments.isEmpty()) {
      return null;
    }
    return String.join(SEPARATOR, pathSegments);
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isEmpty()) {
      return List.of();
    }
    return Arrays.asList(dbData.split(Pattern.quote(SEPARATOR)));
  }
}
