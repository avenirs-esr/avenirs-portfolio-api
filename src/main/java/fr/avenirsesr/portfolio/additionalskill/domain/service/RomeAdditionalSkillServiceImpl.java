package fr.avenirsesr.portfolio.additionalskill.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearch;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.Rome4VersionRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.utils.AdditionalSkillConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class RomeAdditionalSkillServiceImpl implements RomeAdditionalSkillService {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final Rome4VersionRepository rome4VersionRepository;
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;
  private final OpenSearch openSearch;

  @Override
  @CacheEvict(value = AdditionalSkillConstants.INDEX)
  public void cleanAndCreateAdditionalSkillIndex() {
    openSearch.cleanAndCreateAdditionalSkillIndex();
  }

  @Override
  public List<AdditionalSkill> synchronizeAndIndexAdditionalSkills(
      List<AdditionalSkill> additionalSkillList) {
    List<String> skillCodes =
        additionalSkillList.stream()
            .map(additionalSkill -> additionalSkill.getPathSegments().getSkill().getCode())
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    List<AdditionalSkill> existingSkillList =
        additionalSkillRepository.findByPathSegments_Skill_CodeIn(skillCodes);

    Map<String, AdditionalSkill> existingSkillByCode =
        existingSkillList.stream()
            .collect(
                Collectors.toMap(
                    skill -> skill.getPathSegments().getSkill().getCode(), Function.identity()));

    List<AdditionalSkill> toSave = new ArrayList<>();

    for (AdditionalSkill additionalSkill : additionalSkillList) {
      String skillCode = additionalSkill.getPathSegments().getSkill().getCode();
      AdditionalSkill existingSkill = existingSkillByCode.get(skillCode);

      if (existingSkill != null) {
        existingSkill.setPathSegments(additionalSkill.getPathSegments());
        existingSkill.setType(additionalSkill.getType());
        toSave.add(existingSkill);
      } else {
        toSave.add(additionalSkill);
      }
    }

    List<AdditionalSkill> savedAdditionalSkill = additionalSkillRepository.saveAll(toSave);
    openSearch.indexAll(savedAdditionalSkill);
    return savedAdditionalSkill;
  }

  @Override
  public boolean checkRomeVersionUpdated() {
    Rome4Version newVersion = romeAdditionalSkillApi.fetchRomeVersion();

    return rome4VersionRepository
        .findFirstByOrderByVersionDesc()
        .map(
            oldVersion -> {
              if (newVersion.getVersion() > oldVersion.getVersion()) {
                rome4VersionRepository.save(
                    Rome4Version.create(newVersion.getVersion(), newVersion.getLastModifiedDate()));
                return true;
              }
              return false;
            })
        .orElseGet(
            () -> {
              rome4VersionRepository.save(
                  Rome4Version.create(newVersion.getVersion(), newVersion.getLastModifiedDate()));
              return true;
            });
  }
}
