package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.skill.domain.model.Skill;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class SkillDatabaseRepository extends GenericJpaRepositoryAdapter<Skill, SkillEntity>
    implements SkillRepository {
  private static final int MAX_ANCESTOR_DEPTH = 20;
  private static final int CHUNK_SIZE = 1000;

  private final SkillJpaRepository jpaRepository;

  public SkillDatabaseRepository(SkillJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, SkillEntity.class, SkillMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<Skill> findWithAncestorsById(UUID id) {
    return findAllWithAncestorsById(List.of(id)).stream().findFirst();
  }

  @Override
  public List<Skill> findAllWithAncestorsById(List<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    Map<UUID, UUID> parentById = new HashMap<>();
    Set<UUID> collected = new LinkedHashSet<>(ids);
    Collection<UUID> frontier = new LinkedHashSet<>(ids);

    for (int depth = 0; depth < MAX_ANCESTOR_DEPTH && !frontier.isEmpty(); depth++) {
      Set<UUID> next = new LinkedHashSet<>();
      for (SkillParentProjection row : jpaRepository.findParentIdsByIdIn(frontier)) {
        UUID parentId = row.getParentId();
        if (parentId == null) {
          continue;
        }
        parentById.put(row.getId(), parentId);
        if (collected.add(parentId)) {
          next.add(parentId);
        }
      }
      frontier = next;
    }

    Map<UUID, SkillEntity> entityById =
        jpaRepository.findAllByIdInFetchingReferential(collected).stream()
            .collect(Collectors.toMap(SkillEntity::getId, Function.identity()));

    Map<UUID, Skill> assembled = new HashMap<>();
    return ids.stream()
        .map(id -> assemble(id, entityById, parentById, assembled))
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public List<Skill> findAllByReferential(SkillReferential referential) {
    return jpaRepository.findAllByReferentialId(referential.getId()).stream()
        .map(SkillMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public void archiveAll(List<UUID> skillIds) {
    if (skillIds == null || skillIds.isEmpty()) {
      return;
    }
    Instant now = Instant.now();
    for (List<UUID> chunk : chunks(skillIds)) {
      jpaRepository.archiveAllByIdIn(chunk, now);
    }
  }

  private Skill assemble(
      UUID id,
      Map<UUID, SkillEntity> entityById,
      Map<UUID, UUID> parentById,
      Map<UUID, Skill> assembled) {
    if (assembled.containsKey(id)) {
      return assembled.get(id);
    }

    SkillEntity entity = entityById.get(id);
    if (entity == null) {
      return null;
    }

    UUID parentId = parentById.get(id);
    Skill parent = parentId == null ? null : assemble(parentId, entityById, parentById, assembled);

    Skill skill = SkillMapper.INSTANCE.toDomainWithParent(entity, parent);
    assembled.put(id, skill);
    return skill;
  }

  private List<List<UUID>> chunks(List<UUID> ids) {
    List<List<UUID>> chunks = new ArrayList<>();
    for (int start = 0; start < ids.size(); start += CHUNK_SIZE) {
      chunks.add(ids.subList(start, Math.min(start + CHUNK_SIZE, ids.size())));
    }
    return chunks;
  }
}
