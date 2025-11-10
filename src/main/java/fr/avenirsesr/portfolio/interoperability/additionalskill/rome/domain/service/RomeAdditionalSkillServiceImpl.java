package fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.service;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.model.Rome4Version;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.input.RomeAdditionalSkillService;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.RomeAdditionalSkillApi;
import fr.avenirsesr.portfolio.interoperability.additionalskill.rome.domain.port.output.repository.Rome4VersionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RomeAdditionalSkillServiceImpl implements RomeAdditionalSkillService {
  private final AdditionalSkillRepository additionalSkillRepository;
  private final Rome4VersionRepository rome4VersionRepository;
  private final RomeAdditionalSkillApi romeAdditionalSkillApi;

  @Override
  public List<AdditionalSkill> synchronizeAndSaveAdditionalSkills(
      List<AdditionalSkill> additionalSkillList) {
    List<String> skillCodes =
        additionalSkillList.stream()
            .map(AdditionalSkill::getExternalId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

    List<AdditionalSkill> existingSkillList =
        additionalSkillRepository.findAllByExternalId(skillCodes);

    Map<String, AdditionalSkill> existingSkillByCode =
        existingSkillList.stream()
            .collect(Collectors.toMap(AdditionalSkill::getExternalId, Function.identity()));

    List<AdditionalSkill> toSave =
        getAdditionalSkillsToSave(additionalSkillList, existingSkillByCode);
    return additionalSkillRepository.saveAll(toSave);
  }

  @Override
  public boolean checkRomeVersionUpdated() {
    try {
      Rome4Version newVersion = romeAdditionalSkillApi.fetchRomeVersion();

      boolean shouldSave =
          rome4VersionRepository
              .findFirstByOrderByVersionDesc()
              .map(oldVersion -> newVersion.getVersion() > oldVersion.getVersion())
              .orElse(true);

      if (shouldSave) {
        var rome4Version =
            Rome4Version.create(newVersion.getVersion(), newVersion.getLastModifiedDate());
        rome4VersionRepository.save(rome4Version);
      }

      return shouldSave;
    } catch (Exception e) {
      log.error("An error occurred while fetching ROME 4.0 version.", e);
      return false;
    }
  }

  private List<AdditionalSkill> getAdditionalSkillsToSave(
      List<AdditionalSkill> additionalSkillList, Map<String, AdditionalSkill> existingSkillByCode) {
    List<AdditionalSkill> toSave = new ArrayList<>(additionalSkillList.size());

    for (AdditionalSkill additionalSkill : additionalSkillList) {
      AdditionalSkill existingSkill = existingSkillByCode.get(additionalSkill.getExternalId());

      if (existingSkill != null) {
        existingSkill.setAdditionalSkillCategory(
            additionalSkill.getAdditionalSkillCategory().orElse(null));
        existingSkill.setType(additionalSkill.getType());
        toSave.add(existingSkill);
      } else {
        toSave.add(additionalSkill);
      }
    }
    return toSave;
  }
}
