package fr.avenirsesr.portfolio.declaredskill.domain.service;

import fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.client.ExternalSkillClient;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DeclaredSkillSyncServiceImpl implements DeclaredSkillSyncService {

  private final DeclaredSkillRepository declaredSkillRepository;
  private final ExternalSkillClient externalSkillClient;

  @Override
  public Optional<DeclaredSkill> getOrCreateFromExternalSkill(UUID externalSkillId) {
    Optional<DeclaredSkill> existing =
        declaredSkillRepository.findByExternalSkillId(externalSkillId);

    if (existing.isPresent()) {
      log.debug("DeclaredSkill already exists for external skill: {}", externalSkillId);
      return existing;
    }

    log.debug("Fetching external skill from interoperability: {}", externalSkillId);
    Optional<ExternalSkillDTO> externalSkillDTO = externalSkillClient.getById(externalSkillId);

    if (externalSkillDTO.isEmpty()) {
      log.warn("External skill not found in interoperability: {}", externalSkillId);
      return Optional.empty();
    }

    ExternalSkillDTO dto = externalSkillDTO.get();
    DeclaredSkill newSkill =
        DeclaredSkill.create(
            dto.id(),
            dto.title(),
            EExternalSkillType.valueOf(dto.type().name()),
            dto.pathSegments());

    DeclaredSkill saved = declaredSkillRepository.saveOrGet(newSkill);
    log.info("Created new DeclaredSkill from external skill: {}", externalSkillId);
    return Optional.of(saved);
  }
}
