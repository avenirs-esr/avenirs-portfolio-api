package fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additional.skill.domain.port.output.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additional.skill.infrastructure.adapter.model.*;
import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillDatabaseRepository implements AdditionalSkillRepository {
  private static final String JSON_PATH = "/mock/mock-additional-skills.json";

  @Override
  public List<AdditionalSkill> findAll() {
    try (InputStream is = getClass().getResourceAsStream(JSON_PATH)) {
      ObjectMapper mapper = new ObjectMapper();
      List<CompetenceComplementaireDetaillee> entities =
          mapper.readValue(is, new TypeReference<>() {});
      return entities.stream().map(AdditionalSkillMapper::toDomain).toList();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }
}
