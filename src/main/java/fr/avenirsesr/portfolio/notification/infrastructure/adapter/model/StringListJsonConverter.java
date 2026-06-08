package fr.avenirsesr.portfolio.notification.infrastructure.adapter.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<String>> TYPE_REF = new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<String> attribute) {
    if (attribute == null) return null;
    try {
      return MAPPER.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Erreur sérialisation List<String>", e);
    }
  }

  @Override
  public List<String> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;
    try {
      return MAPPER.readValue(dbData, TYPE_REF);
    } catch (Exception e) {
      throw new IllegalArgumentException("Erreur désérialisation List<String>", e);
    }
  }
}
