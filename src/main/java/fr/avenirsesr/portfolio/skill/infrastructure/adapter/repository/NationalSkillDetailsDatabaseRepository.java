package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.skill.domain.model.NationalSkillDetails;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.NationalSkillDetailsRepository;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.NationalSkillDetailsMapper;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.NationalSkillDetailsEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class NationalSkillDetailsDatabaseRepository
    extends GenericJpaRepositoryAdapter<NationalSkillDetails, NationalSkillDetailsEntity>
    implements NationalSkillDetailsRepository {
  private static final int CHUNK_SIZE = 1000;

  private final NationalSkillDetailsJpaRepository jpaRepository;

  public NationalSkillDetailsDatabaseRepository(NationalSkillDetailsJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        NationalSkillDetailsEntity.class,
        NationalSkillDetailsMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Map<UUID, ESkillKind> findKindsBySkillIds(List<UUID> skillIds) {
    if (skillIds == null || skillIds.isEmpty()) {
      return Map.of();
    }

    Map<UUID, ESkillKind> kindBySkillId = new HashMap<>();
    for (List<UUID> chunk : chunks(skillIds)) {
      jpaRepository
          .findKindsBySkillIdIn(chunk)
          .forEach(row -> kindBySkillId.put(row.getSkillId(), row.getKind()));
    }
    return kindBySkillId;
  }

  @Override
  public void deleteAllBySkillIds(List<UUID> skillIds) {
    if (skillIds == null || skillIds.isEmpty()) {
      return;
    }
    for (List<UUID> chunk : chunks(skillIds)) {
      jpaRepository.deleteBySkillIdIn(chunk);
    }
  }

  private List<List<UUID>> chunks(List<UUID> ids) {
    List<List<UUID>> chunks = new ArrayList<>();
    for (int start = 0; start < ids.size(); start += CHUNK_SIZE) {
      chunks.add(ids.subList(start, Math.min(start + CHUNK_SIZE, ids.size())));
    }
    return chunks;
  }
}
