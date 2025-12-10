package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillSyncService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdditionalSkillSyncServiceImpl implements AdditionalSkillSyncService {

  private final AdditionalSkillRepository additionalSkillRepository;
  private final ExternalSkillClient externalSkillClient;

  @Override
  public Optional<AdditionalSkill> getOrCreateFromExternalSkill(UUID externalSkillId) {
    Optional<AdditionalSkill> existing =
        additionalSkillRepository.findByExternalSkillId(externalSkillId);

    if (existing.isPresent()) {
      log.debug("AdditionalSkill already exists for external skill: {}", externalSkillId);
      return existing;
    }

    log.debug("Fetching external skill from interoperability: {}", externalSkillId);
    Optional<ExternalSkillDTO> externalSkillDTO = externalSkillClient.getById(externalSkillId);

    if (externalSkillDTO.isEmpty()) {
      log.warn("External skill not found in interoperability: {}", externalSkillId);
      return Optional.empty();
    }

    ExternalSkillDTO dto = externalSkillDTO.get();
    AdditionalSkill newSkill =
        AdditionalSkill.create(
            dto.id(),
            dto.title(),
            EAdditionalSkillType.valueOf(dto.type().name()),
            dto.pathSegments());

    AdditionalSkill saved = additionalSkillRepository.saveOrGet(newSkill);
    log.info("Created new AdditionalSkill from external skill: {}", externalSkillId);
    return Optional.of(saved);
  }
}
