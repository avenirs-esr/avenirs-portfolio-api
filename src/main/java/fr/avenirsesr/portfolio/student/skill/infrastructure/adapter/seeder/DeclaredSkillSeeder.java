package fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillSyncService;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.mapper.DeclaredSkillMapper;
import fr.avenirsesr.portfolio.student.skill.infrastructure.adapter.model.DeclaredSkillEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeclaredSkillSeeder {
  private final ExternalSkillClient externalSkillClient;
  private final DeclaredSkillSyncService declaredSkillSyncService;

  @Value("${seeder.source:FAKER}")
  private String seederSource;

  public boolean checkInteroperabilityMicroserviceIfNeeded() {
    return "FAKER".equalsIgnoreCase(seederSource)
        || externalSkillClient.checkInteroperabilityMicroservice();
  }

  @Transactional
  public List<DeclaredSkillEntity> seed() {
    log.info("Seeding declared skills from interoperability...");

    var externalSkills = externalSkillClient.getRandomSkills(200);
    log.info("Retrieved {} random external skills", externalSkills.size());

    var declaredSkillEntities =
        externalSkills.stream()
            .map(
                externalSkill ->
                    declaredSkillSyncService
                        .getOrCreateFromExternalSkill(externalSkill.id())
                        .map(DeclaredSkillMapper.INSTANCE::fromDomain)
                        .orElse(null))
            .filter(entity -> entity != null)
            .toList();

    log.info("✔ {} declaredSkill synced", declaredSkillEntities.size());

    return declaredSkillEntities;
  }
}
