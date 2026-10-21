package fr.avenirsesr.portfolio.skill.infrastructure.adapter.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.utils.FileReader;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillReferentialRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SkillCatalogSeeder {
  private static final String PATH_FILE = "seeder/skill-catalog.json";

  private final FileReader fileReader;
  private final SkillSyncService skillSyncService;
  private final SkillRepository skillRepository;
  private final SkillReferentialRepository skillReferentialRepository;

  @Transactional
  public List<Skill> seed() {
    List<Skill> existingSkills = findExistingNationalSkills();

    if (!existingSkills.isEmpty()) {
      log.info(
          "{} national skills already available, skill catalog seeder skipped",
          existingSkills.size());
      return assessableOnly(existingSkills);
    }

    log.info("Seeding the national skill catalog...");

    List<SkillCatalogDTO> catalogs = fileReader.readJSON(PATH_FILE, new TypeReference<>() {});
    List<Skill> skills =
        catalogs.stream()
            .flatMap(catalog -> skillSyncService.applyCatalog(catalog).stream())
            .toList();

    log.info("✔ {} skills created in {} referentials", skills.size(), catalogs.size());

    return assessableOnly(skills);
  }

  private List<Skill> findExistingNationalSkills() {
    return skillReferentialRepository.findAllNational().stream()
        .map(skillRepository::findAllByReferential)
        .flatMap(List::stream)
        .toList();
  }

  private List<Skill> assessableOnly(List<Skill> skills) {
    return skills.stream()
        .filter(Skill::isAssessable)
        .filter(skill -> !skill.isArchived())
        .toList();
  }
}
