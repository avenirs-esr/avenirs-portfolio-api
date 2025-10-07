package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.seeder.fake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.seeder.infrastructure.adapter.FakeExternalSource;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.CompetenceComplementaireDetaillee;
import java.io.InputStream;
import java.util.List;

public class FakeAdditionalSkill {
  private static final String JSON_PATH = "/mock/mock-additional-skills.json";

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final AdditionalSkillEntity additionalSkill;

  private FakeAdditionalSkill(AdditionalSkillEntity additionalSkill) {
    this.additionalSkill = additionalSkill;
  }

  public static List<FakeAdditionalSkill> of() {
    try (InputStream is = FakeExternalSource.class.getResourceAsStream(JSON_PATH)) {
      List<CompetenceComplementaireDetaillee> entities =
          objectMapper.readValue(is, new TypeReference<>() {});

      List<AdditionalSkillEntity> additionalSkillEntities =
          entities.stream()
              .map(AdditionalSkillMapper::toDomain)
              .map(AdditionalSkillMapper::fromDomain)
              .toList();

      return additionalSkillEntities.stream().map(FakeAdditionalSkill::new).toList();
    } catch (Exception e) {
      throw new RuntimeException("Unable to load mock additional skills", e);
    }
  }

  public AdditionalSkillEntity toEntity() {
    return additionalSkill;
  }
}
