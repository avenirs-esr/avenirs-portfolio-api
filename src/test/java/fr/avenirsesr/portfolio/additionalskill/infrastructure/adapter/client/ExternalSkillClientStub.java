package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillCategoryDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDetailsDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillCategoryType;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Primary
public class ExternalSkillClientStub extends ExternalSkillClient {

  @Value("${external-skill.not-found-id}")
  private UUID notFoundExternalSkillId;

  public ExternalSkillClientStub() {
    super(null);
  }

  @Override
  public Optional<ExternalSkillDTO> getById(UUID externalSkillId) {
    if (externalSkillId.equals(notFoundExternalSkillId)) {
      return Optional.empty();
    }
    return Optional.of(
        new ExternalSkillDTO(
            externalSkillId,
            "External Skill " + externalSkillId,
            List.of("Domaine", "Issue", "Target"),
            EExternalSkillType.ROME4));
  }

  @Override
  public Optional<ExternalSkillDetailsDTO> getExternalSkillDetails(UUID externalSkillId) {
    List<ExternalSkillCategoryDTO> categoryPath =
        List.of(
            new ExternalSkillCategoryDTO("Domaine 1", EExternalSkillCategoryType.DOMAIN),
            new ExternalSkillCategoryDTO("Issue 1", EExternalSkillCategoryType.ISSUE),
            new ExternalSkillCategoryDTO("Target 1", EExternalSkillCategoryType.TARGET));

    return Optional.of(
        new ExternalSkillDetailsDTO(
            externalSkillId, "External Skill Details", categoryPath, EExternalSkillType.ROME4));
  }

  @Override
  public List<ExternalSkillDTO> getRandomSkills(int count) {
    List<ExternalSkillDTO> skills = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      skills.add(
          new ExternalSkillDTO(
              UUID.randomUUID(),
              "External Skill " + (i + 1),
              List.of("Domaine", "Issue", "Target"),
              EExternalSkillType.ROME4));
    }
    return skills;
  }
}
