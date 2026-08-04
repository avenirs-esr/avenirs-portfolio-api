package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class SelfKnowledgeCategoryListJsonConverter
    implements AttributeConverter<List<ESelfKnowledgeCategory>, String> {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<ESelfKnowledgeCategory>> TYPE_REF =
      new TypeReference<>() {};

  @Override
  public String convertToDatabaseColumn(List<ESelfKnowledgeCategory> attribute) {
    if (attribute == null) return null;
    try {
      return MAPPER.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Erreur sérialisation List<ESelfKnowledgeCategory>", e);
    }
  }

  @Override
  public List<ESelfKnowledgeCategory> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.isBlank()) return null;
    try {
      return MAPPER.readValue(dbData, TYPE_REF);
    } catch (Exception e) {
      throw new IllegalArgumentException("Erreur désérialisation List<ESelfKnowledgeCategory>", e);
    }
  }
}
