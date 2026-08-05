package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AssociationsJsonConverter implements AttributeConverter<AssociationsJson, String> {

  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

  @Override
  public String convertToDatabaseColumn(AssociationsJson attribute) {
    if (attribute == null) return null;
    try {
      return MAPPER.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Erreur sérialisation AssociationsJson", e);
    }
  }

  @Override
  public AssociationsJson convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;
    try {
      return MAPPER.readValue(dbData, AssociationsJson.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Erreur désérialisation AssociationsJson", e);
    }
  }
}
