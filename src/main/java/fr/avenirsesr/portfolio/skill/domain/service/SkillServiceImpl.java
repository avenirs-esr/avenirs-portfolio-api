package fr.avenirsesr.portfolio.skill.domain.service;

import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.skill.domain.data.SkillPathSegment;
import fr.avenirsesr.portfolio.skill.domain.exception.SkillNotFoundException;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillService;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
  private final SkillRepository skillRepository;
  private final NationalSkillDetailsRepository nationalSkillDetailsRepository;

  @Override
  public Skill getWithAncestorsById(UUID id) {
    return skillRepository.findWithAncestorsById(id).orElseThrow(SkillNotFoundException::new);
  }

  @Override
  public List<Skill> getAllWithAncestorsById(List<UUID> ids) {
    return skillRepository.findAllWithAncestorsById(ids);
  }

  @Override
  public List<SkillPathSegment> getDetailedPath(Skill skill) {
    return getDetailedPaths(List.of(skill)).getOrDefault(skill.getId(), List.of());
  }

  @Override
  public Map<UUID, List<SkillPathSegment>> getDetailedPaths(List<Skill> skills) {
    List<UUID> ancestorIds =
        skills.stream()
            .flatMap(skill -> skill.getAncestors().stream())
            .map(Skill::getId)
            .distinct()
            .toList();

    Map<UUID, ESkillKind> kindBySkillId =
        nationalSkillDetailsRepository.findKindsBySkillIds(ancestorIds);

    Map<UUID, List<SkillPathSegment>> pathBySkillId = new LinkedHashMap<>();
    skills.forEach(
        skill ->
            pathBySkillId.put(
                skill.getId(),
                skill.getAncestors().stream()
                    .map(
                        ancestor ->
                            new SkillPathSegment(
                                ancestor.getTitle(), kindBySkillId.get(ancestor.getId())))
                    .toList()));

    return pathBySkillId;
  }
}
