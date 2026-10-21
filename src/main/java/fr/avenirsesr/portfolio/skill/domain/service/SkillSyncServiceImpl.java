package fr.avenirsesr.portfolio.skill.domain.service;

import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillCatalogDTO;
import fr.avenirsesr.portfolio.common.skill.application.adapter.dto.SkillNodeDTO;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.model.NationalSkillDetails;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import fr.avenirsesr.portfolio.skill.domain.port.input.SkillSyncService;
import fr.avenirsesr.portfolio.skill.domain.port.output.client.SkillCatalogClient;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillReferentialRepository;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SkillSyncServiceImpl implements SkillSyncService {
  private static final List<ESkillReferentialType> NATIONAL_TYPES =
      List.of(
          ESkillReferentialType.ROME4,
          ESkillReferentialType.XXI,
          ESkillReferentialType.CASOC,
          ESkillReferentialType.CASOL);
  private static final int SAVE_CHUNK_SIZE = 500;

  private final SkillCatalogClient skillCatalogClient;
  private final SkillRepository skillRepository;
  private final SkillReferentialRepository skillReferentialRepository;
  private final NationalSkillDetailsRepository nationalSkillDetailsRepository;

  @Override
  public void syncNationalCatalogs() {
    NATIONAL_TYPES.forEach(this::syncNationalCatalog);
  }

  @Override
  public void syncNationalCatalog(ESkillReferentialType type) {
    Optional<SkillCatalogDTO> catalog = skillCatalogClient.fetchNationalCatalog(type);

    if (catalog.isEmpty()) {
      log.warn("No {} skill catalog available, synchronization skipped", type);
      return;
    }

    applyCatalog(catalog.get());
  }

  @Override
  public List<Skill> applyCatalog(SkillCatalogDTO catalog) {
    SkillReferential referential = upsertReferential(catalog);

    Map<UUID, Skill> existingById =
        skillRepository.findAllByReferential(referential).stream()
            .collect(Collectors.toMap(Skill::getId, Function.identity()));

    Map<UUID, Skill> syncedById = new LinkedHashMap<>();
    List<Skill> toSave = new ArrayList<>();

    for (SkillNodeDTO node : orderByHierarchy(catalog.nodes())) {
      Skill parent = node.parentId() == null ? null : syncedById.get(node.parentId());
      Skill skill = merge(existingById.get(node.id()), node, referential, parent);
      syncedById.put(node.id(), skill);
      toSave.add(skill);
    }

    saveSkillsInChunks(toSave);

    List<UUID> obsoleteIds =
        existingById.keySet().stream().filter(id -> !syncedById.containsKey(id)).toList();
    skillRepository.archiveAll(obsoleteIds);

    replaceNationalDetails(catalog, syncedById, existingById.keySet());

    log.info(
        "✔ {} skill catalog synchronized: {} skills, {} archived",
        catalog.type(),
        syncedById.size(),
        obsoleteIds.size());

    return List.copyOf(syncedById.values());
  }

  private SkillReferential upsertReferential(SkillCatalogDTO catalog) {
    return skillReferentialRepository
        .findNationalByType(catalog.type())
        .map(
            existing -> {
              existing.setExternalId(catalog.externalId());
              existing.setTitle(catalog.title());
              return skillReferentialRepository.save(existing);
            })
        .orElseGet(
            () ->
                skillReferentialRepository.save(
                    SkillReferential.create(
                        catalog.type(), catalog.externalId(), catalog.title(), null)));
  }

  private Skill merge(
      Skill existing, SkillNodeDTO node, SkillReferential referential, Skill parent) {
    if (existing == null) {
      return Skill.create(
          node.id(),
          referential,
          node.externalCode(),
          node.title(),
          node.description(),
          parent,
          node.assessable());
    }

    existing.setReferential(referential);
    existing.setExternalCode(node.externalCode());
    existing.setTitle(node.title());
    existing.setDescription(node.description());
    existing.setParent(parent);
    existing.setAssessable(node.assessable());
    existing.setArchived(false);
    return existing;
  }

  private void replaceNationalDetails(
      SkillCatalogDTO catalog, Map<UUID, Skill> syncedById, Set<UUID> previousIds) {
    Set<UUID> impactedIds = new LinkedHashSet<>(syncedById.keySet());
    impactedIds.addAll(previousIds);
    nationalSkillDetailsRepository.deleteAllBySkillIds(List.copyOf(impactedIds));

    List<NationalSkillDetails> details =
        catalog.nodes().stream()
            .filter(node -> node.kind() != null)
            .map(
                node ->
                    Optional.ofNullable(syncedById.get(node.id()))
                        .map(skill -> NationalSkillDetails.create(skill, node.kind()))
                        .orElse(null))
            .filter(Objects::nonNull)
            .toList();

    for (int start = 0; start < details.size(); start += SAVE_CHUNK_SIZE) {
      nationalSkillDetailsRepository.saveAll(
          details.subList(start, Math.min(start + SAVE_CHUNK_SIZE, details.size())));
    }
  }

  private void saveSkillsInChunks(List<Skill> skills) {
    for (int start = 0; start < skills.size(); start += SAVE_CHUNK_SIZE) {
      skillRepository.saveAll(
          skills.subList(start, Math.min(start + SAVE_CHUNK_SIZE, skills.size())));
    }
  }

  private List<SkillNodeDTO> orderByHierarchy(List<SkillNodeDTO> nodes) {
    Map<UUID, SkillNodeDTO> nodeById =
        nodes.stream()
            .filter(node -> node.id() != null)
            .collect(
                Collectors.toMap(
                    SkillNodeDTO::id, Function.identity(), (first, second) -> first, HashMap::new));

    List<SkillNodeDTO> ordered = new ArrayList<>(nodeById.size());
    Set<UUID> emitted = new LinkedHashSet<>();

    for (SkillNodeDTO node : nodes) {
      emitWithAncestors(node, nodeById, emitted, ordered);
    }

    return ordered;
  }

  private void emitWithAncestors(
      SkillNodeDTO node,
      Map<UUID, SkillNodeDTO> nodeById,
      Set<UUID> emitted,
      List<SkillNodeDTO> ordered) {
    if (node == null || node.id() == null || emitted.contains(node.id())) {
      return;
    }

    List<SkillNodeDTO> branch = new ArrayList<>();
    Set<UUID> visited = new LinkedHashSet<>();
    SkillNodeDTO current = node;

    while (current != null && !emitted.contains(current.id()) && visited.add(current.id())) {
      branch.add(current);
      current = current.parentId() == null ? null : nodeById.get(current.parentId());
    }

    for (int index = branch.size() - 1; index >= 0; index--) {
      SkillNodeDTO branchNode = branch.get(index);
      if (emitted.add(branchNode.id())) {
        ordered.add(branchNode);
      }
    }
  }
}
